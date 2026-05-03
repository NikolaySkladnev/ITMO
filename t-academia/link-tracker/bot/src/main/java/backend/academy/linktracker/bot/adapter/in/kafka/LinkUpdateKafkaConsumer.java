package backend.academy.linktracker.bot.adapter.in.kafka;

import backend.academy.linktracker.bot.adapter.in.kafka.dto.LinkUpdateMessage;
import backend.academy.linktracker.bot.adapter.out.kafka.DeadLetterQueuePublisher;
import backend.academy.linktracker.bot.application.port.in.ProcessBotUpdateUseCase;
import backend.academy.linktracker.bot.infrastructure.properties.KafkaNotificationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true")
public class LinkUpdateKafkaConsumer {
    private static final String DESERIALIZATION_ERROR = "DESERIALIZATION_ERROR";
    private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    private static final String PROCESSING_ERROR = "PROCESSING_ERROR";

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final ProcessBotUpdateUseCase botUpdateUseCase;
    private final DeadLetterQueuePublisher deadLetterQueuePublisher;
    private final KafkaNotificationProperties properties;

    @KafkaListener(topics = "${app.kafka.topics.updates}", groupId = "${app.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        LinkUpdateMessage message = deserialize(record);
        if (message == null) {
            return;
        }

        if (!validate(record, message)) {
            return;
        }

        processWithRetry(record, message);
    }

    private LinkUpdateMessage deserialize(ConsumerRecord<String, String> record) {
        if (record.value() == null || record.value().isBlank()) {
            deadLetterQueuePublisher.publish(
                    record, DESERIALIZATION_ERROR, new IllegalArgumentException("Kafka message value is empty"));
            return null;
        }

        try {
            return objectMapper.readValue(record.value(), LinkUpdateMessage.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize link update message from Kafka", e);
            deadLetterQueuePublisher.publish(record, DESERIALIZATION_ERROR, e);
            return null;
        }
    }

    private boolean validate(ConsumerRecord<String, String> record, LinkUpdateMessage message) {
        Set<ConstraintViolation<LinkUpdateMessage>> violations = validator.validate(message);
        if (violations.isEmpty()) {
            return true;
        }

        String validationMessage = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        log.warn("Invalid link update message from Kafka: {}", validationMessage);
        deadLetterQueuePublisher.publish(record, VALIDATION_ERROR, new IllegalArgumentException(validationMessage));
        return false;
    }

    private void processWithRetry(ConsumerRecord<String, String> record, LinkUpdateMessage message) {
        int maxAttempts = Math.max(1, properties.getConsumer().getMaxAttempts());
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                botUpdateUseCase.process(message.id(), message.url(), message.description(), message.tgChatIds());

                if (attempt > 1) {
                    log.info("Kafka message was processed after retry: attempt={}", attempt);
                }

                return;
            } catch (Exception e) {
                lastException = e;
                log.warn(
                        "Failed to process Kafka link update message: attempt={}, maxAttempts={}",
                        attempt,
                        maxAttempts,
                        e);
            }
        }

        deadLetterQueuePublisher.publish(record, PROCESSING_ERROR, lastException);
    }
}
