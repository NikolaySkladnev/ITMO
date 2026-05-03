package backend.academy.linktracker.bot.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import backend.academy.linktracker.bot.BotApplication;
import backend.academy.linktracker.bot.support.TelegramTestSupport;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@Tag("e2e")
@SpringBootTest(classes = BotApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BotRestE2ETest {

    @MockitoBean
    private TelegramBot telegramBot;

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() {
        reset(telegramBot);
    }

    @Test
    void shouldAcceptUpdateAndSendNotificationsToAllChats() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/updates"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                {
                  "id": 1,
                  "url": "https://github.com/openai/openai-java",
                  "description": "Repo updated",
                  "tgChatIds": [1, 2]
                }
                """))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(captor.capture());

        List<String> texts =
                captor.getAllValues().stream().map(TelegramTestSupport::textOf).toList();

        assertThat(texts)
                .containsOnly("Обнаружено обновление по ссылке: https://github.com/openai/openai-java\nRepo updated");
    }

    @Test
    void shouldRejectInvalidRequestBody() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/updates"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                {
                  "id": "wrong-type",
                  "url": 42,
                  "tgChatIds": "not-an-array"
                }
                """))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
        verifyNoInteractions(telegramBot);
    }
}
