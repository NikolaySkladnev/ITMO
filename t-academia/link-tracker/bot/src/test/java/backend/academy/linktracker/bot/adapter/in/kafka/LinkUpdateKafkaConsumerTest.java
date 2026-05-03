package backend.academy.linktracker.bot.adapter.in.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.adapter.out.kafka.DeadLetterQueuePublisher;
import backend.academy.linktracker.bot.application.port.in.ProcessBotUpdateUseCase;
import backend.academy.linktracker.bot.infrastructure.properties.KafkaNotificationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LinkUpdateKafkaConsumerTest {

    private ProcessBotUpdateUseCase botUpdateUseCase;
    private DeadLetterQueuePublisher deadLetterQueuePublisher;
    private LinkUpdateKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        botUpdateUseCase = Mockito.mock(ProcessBotUpdateUseCase.class);
        deadLetterQueuePublisher = Mockito.mock(DeadLetterQueuePublisher.class);

        KafkaNotificationProperties properties = new KafkaNotificationProperties();
        properties.getConsumer().setMaxAttempts(3);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        consumer = new LinkUpdateKafkaConsumer(
                new ObjectMapper(), validator, botUpdateUseCase, deadLetterQueuePublisher, properties);
    }

    @Test
    void shouldProcessValidMessage() {
        ConsumerRecord<String, String> record = record("""
                {
                  "id": 1,
                  "url": "https://github.com/openai/openai-java",
                  "description": "Repo updated",
                  "tgChatIds": [10, 20]
                }
                """);

        consumer.consume(record);

        verify(botUpdateUseCase)
                .process(1L, "https://github.com/openai/openai-java", "Repo updated", List.of(10L, 20L));

        verify(deadLetterQueuePublisher, never()).publish(any(), any(), any());
    }

    @Test
    void shouldSendMessageToDlqWhenJsonIsInvalid() {
        ConsumerRecord<String, String> record = record("{ wrong-json");

        consumer.consume(record);

        verify(botUpdateUseCase, never()).process(any(Long.class), any(), any(), any());
        verify(deadLetterQueuePublisher).publish(eq(record), eq("DESERIALIZATION_ERROR"), any(Exception.class));
    }

    @Test
    void shouldSendMessageToDlq() {
        ConsumerRecord<String, String> record = record("""
                {
                  "id": null,
                  "url": "",
                  "description": "Repo updated",
                  "tgChatIds": []
                }
                """);

        consumer.consume(record);

        verify(botUpdateUseCase, never()).process(any(Long.class), any(), any(), any());
        verify(deadLetterQueuePublisher).publish(eq(record), eq("VALIDATION_ERROR"), any(Exception.class));
    }

    @Test
    void shouldRetryProcessingError() {
        ConsumerRecord<String, String> record = record("""
                {
                  "id": 1,
                  "url": "https://github.com/openai/openai-java",
                  "description": "Repo updated",
                  "tgChatIds": [10]
                }
                """);

        Mockito.doThrow(new RuntimeException("telegram failed"))
                .when(botUpdateUseCase)
                .process(1L, "https://github.com/openai/openai-java", "Repo updated", List.of(10L));

        consumer.consume(record);

        verify(botUpdateUseCase, times(3))
                .process(1L, "https://github.com/openai/openai-java", "Repo updated", List.of(10L));

        verify(deadLetterQueuePublisher).publish(eq(record), eq("PROCESSING_ERROR"), any(Exception.class));
    }

    private ConsumerRecord<String, String> record(String value) {
        return new ConsumerRecord<>("link-updates", 0, 1L, "key", value);
    }
}
