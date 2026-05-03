package backend.academy.linktracker.bot.adapter.out.rest;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import backend.academy.linktracker.bot.infrastructure.properties.ScrapperRestProperties;
import io.grpc.Status;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.scrapper", name = "transport", havingValue = "rest")
public class ScrapperRestClient implements ScrapperClient {

    private final RestClient restClient;

    public ScrapperRestClient(RestClient.Builder builder, ScrapperRestProperties properties) {
        this.restClient = builder.baseUrl(properties.getBaseUrl()).build();
    }

    @Override
    public void registerChat(long chatId) {
        try {
            restClient.post().uri("/tg-chat/{id}", chatId).retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 409) {
                return;
            }
            throw new RuntimeException(
                    "Scrapper REST register chat failed: " + e.getStatusCode().value(), e);
        }
    }

    @Override
    public void deleteChat(long chatId) {
        try {
            restClient.delete().uri("/tg-chat/{id}", chatId).retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new RuntimeException(
                    "Scrapper REST delete chat failed: " + e.getStatusCode().value(), e);
        }
    }

    @Override
    public void addLink(long chatId, String url, List<String> tags) {
        try {
            restClient
                    .post()
                    .uri("/links")
                    .header("Tg-Chat-Id", String.valueOf(chatId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AddLinkRequest(url, tags, List.of()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 409) {
                throw Status.ALREADY_EXISTS
                        .withDescription("Link is already tracked")
                        .asRuntimeException();
            }
            if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 404) {
                throw new IllegalArgumentException("Failed to add link: " + e.getResponseBodyAsString(), e);
            }
            throw new RuntimeException(
                    "Scrapper REST add link failed: " + e.getStatusCode().value(), e);
        }
    }

    @Override
    public void removeLink(long chatId, String url) {
        try {
            restClient
                    .method(org.springframework.http.HttpMethod.DELETE)
                    .uri("/links")
                    .header("Tg-Chat-Id", String.valueOf(chatId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RemoveLinkRequest(url))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 404) {
                throw new IllegalArgumentException("Failed to remove link: " + e.getResponseBodyAsString(), e);
            }
            throw new RuntimeException(
                    "Scrapper REST remove link failed: " + e.getStatusCode().value(), e);
        }
    }

    @Override
    public List<TrackedLinkDto> getLinks(long chatId) {
        try {
            ListLinksResponse response = restClient
                    .get()
                    .uri("/links")
                    .header("Tg-Chat-Id", String.valueOf(chatId))
                    .retrieve()
                    .body(ListLinksResponse.class);

            if (response == null || response.links() == null) {
                return List.of();
            }

            return response.links().stream()
                    .map(link ->
                            new TrackedLinkDto(link.id(), link.url(), link.tags() == null ? List.of() : link.tags()))
                    .toList();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 404) {
                throw new IllegalArgumentException("Failed to get links: " + e.getResponseBodyAsString(), e);
            }
            throw new RuntimeException(
                    "Scrapper REST get links failed: " + e.getStatusCode().value(), e);
        }
    }

    private record AddLinkRequest(String link, List<String> tags, List<String> filters) {}

    private record RemoveLinkRequest(String link) {}

    private record LinkResponse(long id, String url, List<String> tags, List<String> filters) {}

    private record ListLinksResponse(List<LinkResponse> links, Integer size) {}
}
