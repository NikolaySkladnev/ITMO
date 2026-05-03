package backend.academy.linktracker.bot.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class KafkaToBotIntegrationTest {

    private static final long CHAT_ID = 123456L;
    private static final String URL = "https://example.com/post/1";
    private static final String DESCRIPTION = "test update";

    @Container
    static final ConfluentKafkaContainer KAFKA =
            new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("app.kafka.enabled", () -> "true");

        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);

        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.listener.missing-topics-fatal", () -> "false");

        registry.add("app.kafka.topics.link-updates", () -> "link-updates");
        registry.add("app.kafka.topics.dead-letter-queue", () -> "link-updates-dlq");
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoBean
    private TelegramBot telegramBot;

    @Value("${app.kafka.topics.link-updates}")
    private String linkUpdatesTopic;

    @BeforeEach
    void setUp() {
        reset(telegramBot);

        SendResponse response = org.mockito.Mockito.mock(SendResponse.class);
        when(response.isOk()).thenReturn(true);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);
    }

    @Test
    void shouldSendTelegramMessageWhenLinkUpdateReceivedFromKafka() throws Exception {
        String payload = """
            {
              "id": 1,
              "url": "%s",
              "description": "%s",
              "tgChatIds": [%d]
            }
            """.formatted(URL, DESCRIPTION, CHAT_ID);

        kafkaTemplate.send(linkUpdatesTopic, "1", payload).get(10, TimeUnit.SECONDS);

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        verify(telegramBot, timeout(Duration.ofSeconds(15).toMillis()).atLeastOnce())
                .execute(messageCaptor.capture());

        Map<String, Object> params = messageCaptor.getValue().getParameters();

        assertThat(String.valueOf(params.get("chat_id"))).isEqualTo(String.valueOf(CHAT_ID));
        assertThat(String.valueOf(params.get("text"))).contains(URL).contains(DESCRIPTION);
    }
}
