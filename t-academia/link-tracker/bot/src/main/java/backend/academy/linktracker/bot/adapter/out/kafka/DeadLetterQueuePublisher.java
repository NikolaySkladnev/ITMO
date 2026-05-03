package backend.academy.linktracker.bot.adapter.out.kafka;

import backend.academy.linktracker.bot.adapter.out.kafka.exception.DlqPublishingException;
import backend.academy.linktracker.bot.infrastructure.properties.KafkaNotificationProperties;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class DeadLetterQueuePublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaNotificationProperties properties;

    public void publish(ConsumerRecord<String, String> sourceRecord, String reason, Exception exception) {
        ProducerRecord<String, String> dlqRecord =
                new ProducerRecord<>(properties.getTopics().getDlq(), sourceRecord.key(), sourceRecord.value());

        addHeader(dlqRecord, "x-original-topic", sourceRecord.topic());
        addHeader(dlqRecord, "x-original-partition", String.valueOf(sourceRecord.partition()));
        addHeader(dlqRecord, "x-original-offset", String.valueOf(sourceRecord.offset()));
        addHeader(dlqRecord, "x-dlq-reason", reason);

        if (exception != null) {
            addHeader(dlqRecord, "x-exception-class", exception.getClass().getName());
            addHeader(dlqRecord, "x-exception-message", exception.getMessage());
        }

        try {
            kafkaTemplate.send(dlqRecord).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while sending message to DLQ", e);
        } catch (ExecutionException e) {
            throw new DlqPublishingException("Failed to send message to DLQ", e);
        }
    }

    private void addHeader(ProducerRecord<String, String> record, String name, String value) {
        if (value == null) {
            return;
        }
        record.headers().add(name, value.getBytes(StandardCharsets.UTF_8));
    }
}
