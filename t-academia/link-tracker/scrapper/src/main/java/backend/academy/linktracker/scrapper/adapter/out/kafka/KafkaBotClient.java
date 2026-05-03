package backend.academy.linktracker.scrapper.adapter.out.kafka;

import backend.academy.linktracker.scrapper.adapter.out.kafka.dto.LinkUpdateMessage;
import backend.academy.linktracker.scrapper.application.dto.UpdateNotification;
import backend.academy.linktracker.scrapper.application.port.out.BotClient;
import backend.academy.linktracker.scrapper.infrastructure.properties.KafkaNotificationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.bot", name = "transport", havingValue = "kafka", matchIfMissing = true)
public class KafkaBotClient implements BotClient {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaNotificationProperties properties;

    @Override
    public void sendUpdate(UpdateNotification update) {
        LinkUpdateMessage message =
                new LinkUpdateMessage(update.id(), update.url(), update.description(), update.chatIds());

        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(properties.getTopics().getUpdates(), update.url(), payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize link update message", e);
        }
    }
}
