package backend.academy.linktracker.scrapper.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.adapter.out.http.github.GitHubHttpClient;
import backend.academy.linktracker.scrapper.adapter.out.http.stack.overflow.StackOverFlowHttpClient;
import backend.academy.linktracker.scrapper.application.port.in.CheckUpdatesUseCase;
import backend.academy.linktracker.scrapper.application.port.out.BotClient;
import backend.academy.linktracker.scrapper.support.AbstractPostgresContainerTest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Tag("e2e")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "app.github.token=test-token",
            "app.stackoverflow.key=test-key",
            "app.stackoverflow.access-token=test-access-token",
            "app.bot.transport=rest",
            "app.bot.rest.base-url=http://localhost:65535",
            "app.scheduler.enabled=false",
            "app.scheduler.interval=99999999",
            "spring.grpc.server.port=0",
            "spring.kafka.listener.auto-startup=false",
            "spring.autoconfigure.exclude=org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration"
        })
class ScrapperRestE2ETest extends AbstractPostgresContainerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private GitHubHttpClient gitHubClient;

    @MockitoBean
    private StackOverFlowHttpClient stackOverFlowClient;

    @MockitoBean
    private BotClient botClient;

    @MockitoBean
    private CheckUpdatesUseCase checkUpdatesUseCase;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() {
        reset(gitHubClient, stackOverFlowClient, botClient, checkUpdatesUseCase);

        when(gitHubClient.getRepository("openai", "openai-java")).thenReturn(Instant.parse("2026-03-01T10:15:30Z"));
    }

    @Test
    void shouldRegisterAddListAndRemoveLinkThroughRealHttpApi() throws IOException, InterruptedException {
        long chatId = 1001L;
        String url = "https://github.com/openai/openai-java";

        HttpResponse<String> registerResponse = registerChat(chatId);
        assertThat(registerResponse.statusCode()).isEqualTo(200);

        HttpResponse<String> addResponse = addLink(chatId, url);
        assertThat(addResponse.statusCode()).isEqualTo(200);
        assertThat(addResponse.body()).contains(url);

        HttpResponse<String> listResponse = listLinks(chatId);
        assertThat(listResponse.statusCode()).isEqualTo(200);
        assertThat(listResponse.body()).contains(url);
        assertThat(listResponse.body()).contains("work");

        HttpResponse<String> removeResponse = removeLink(chatId, url);
        assertThat(removeResponse.statusCode()).isEqualTo(200);
        assertThat(removeResponse.body()).contains(url);

        HttpResponse<String> listAfterRemoveResponse = listLinks(chatId);
        assertThat(listAfterRemoveResponse.statusCode()).isEqualTo(200);
        assertThat(listAfterRemoveResponse.body()).doesNotContain(url);
    }

    @Test
    void shouldReturn404WhenAddingLinkToNonExistingChat() throws IOException, InterruptedException {
        HttpResponse<String> response = addLink(999_999L, "https://github.com/openai/openai-java");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    void shouldReturn409WhenAddingDuplicateLink() throws IOException, InterruptedException {
        long chatId = 1002L;
        String url = "https://github.com/openai/openai-java";

        assertThat(registerChat(chatId).statusCode()).isEqualTo(200);
        assertThat(addLink(chatId, url).statusCode()).isEqualTo(200);

        HttpResponse<String> duplicateResponse = addLink(chatId, url);

        assertThat(duplicateResponse.statusCode()).isEqualTo(409);
    }

    private HttpResponse<String> registerChat(long chatId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/tg-chat/" + chatId))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> addLink(long chatId, String link) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/links"))
                .header("Content-Type", "application/json")
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                          "link": "%s",
                          "tags": ["work"],
                          "filters": []
                        }
                        """.formatted(link)))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> removeLink(long chatId, String link) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/links"))
                .header("Content-Type", "application/json")
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .method("DELETE", HttpRequest.BodyPublishers.ofString("""
                        {
                          "link": "%s"
                        }
                        """.formatted(link)))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> listLinks(long chatId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/links"))
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
