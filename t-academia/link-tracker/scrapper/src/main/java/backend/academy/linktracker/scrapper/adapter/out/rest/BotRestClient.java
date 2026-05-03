package backend.academy.linktracker.scrapper.adapter.out.rest;

import backend.academy.linktracker.scrapper.application.dto.UpdateNotification;
import backend.academy.linktracker.scrapper.application.port.out.BotClient;
import backend.academy.linktracker.scrapper.infrastructure.properties.BotRestProperties;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@ConditionalOnProperty(prefix = "app.bot", name = "transport", havingValue = "rest")
public class BotRestClient implements BotClient {

    private final RestClient restClient;

    public BotRestClient(RestClient.Builder builder, BotRestProperties properties) {
        this.restClient = builder.baseUrl(properties.getBaseUrl()).build();
    }

    @Override
    public void sendUpdate(UpdateNotification update) {
        try {
            restClient
                    .post()
                    .uri("/updates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new LinkUpdateRequest(update.id(), update.url(), update.description(), update.chatIds()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new RuntimeException(
                    "Bot REST API returned status " + e.getStatusCode().value(), e);
        }
    }

    private record LinkUpdateRequest(long id, String url, String description, List<Long> tgChatIds) {}
}
