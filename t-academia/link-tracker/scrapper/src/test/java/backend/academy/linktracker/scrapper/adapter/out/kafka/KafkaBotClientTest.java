package backend.academy.linktracker.scrapper.adapter.out.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.scrapper.application.dto.UpdateNotification;
import backend.academy.linktracker.scrapper.infrastructure.properties.KafkaNotificationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class KafkaBotClientTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaBotClient kafkaBotClient;

    @BeforeEach
    void setUp() {
        KafkaNotificationProperties properties = new KafkaNotificationProperties();
        properties.getTopics().setUpdates("link-updates");

        kafkaBotClient = new KafkaBotClient(kafkaTemplate, new ObjectMapper(), properties);
    }

    @Test
    void shouldSendLinkUpdateMessageToKafkaAsJson() {
        UpdateNotification update = new UpdateNotification(
                1L, "https://github.com/openai/openai-java", "Repo updated", List.of(1001L, 1002L));

        kafkaBotClient.sendUpdate(update);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), payloadCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("link-updates");
        assertThat(keyCaptor.getValue()).isEqualTo("https://github.com/openai/openai-java");

        assertThat(payloadCaptor.getValue())
                .contains("\"id\":1")
                .contains("\"url\":\"https://github.com/openai/openai-java\"")
                .contains("\"description\":\"Repo updated\"")
                .contains("\"tgChatIds\":[1001,1002]");
    }
}
