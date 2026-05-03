package backend.academy.linktracker.scrapper.adapter.out.http.github;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.exception.ExternalServiceException;
import backend.academy.linktracker.scrapper.application.port.out.GitHubClient;
import backend.academy.linktracker.scrapper.application.port.out.GitHubEventsClient;
import backend.academy.linktracker.scrapper.infrastructure.properties.GithubProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GitHubHttpClient implements GitHubClient, GitHubEventsClient {

    private static final int PER_PAGE = 100;
    private static final String API_VERSION = "2022-11-28";

    private final RestClient restClient;

    @Autowired
    public GitHubHttpClient(RestClient.Builder builder, GithubProperties properties) {
        this.restClient = buildClient(builder, properties.getToken());
    }

    public GitHubHttpClient(RestClient.Builder builder) {
        this.restClient = buildClient(builder, null);
    }

    @Override
    public Instant getRepository(String owner, String repo) {
        try {
            GitRepository response = restClient
                    .get()
                    .uri("/repos/{owner}/{repo}", owner, repo)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, httpResponse) -> {
                        throw new ExternalServiceException("GitHub API returned status "
                                + httpResponse.getStatusCode().value());
                    })
                    .body(GitRepository.class);

            if (response == null || response.updatedAt() == null) {
                throw new ExternalServiceException("GitHub API returned an invalid body");
            }

            return response.updatedAt().toInstant();
        } catch (ExternalServiceException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ExternalServiceException("GitHub API request failed", e);
        } catch (Exception e) {
            throw new ExternalServiceException("GitHub API response handling failed", e);
        }
    }

    @Override
    public List<LinkUpdateEvent> getUpdates(String owner, String repo, Instant since) {
        try {
            List<LinkUpdateEvent> result = new ArrayList<>();
            int page = 1;

            while (true) {
                int finalPage = page;
                GitHubIssueResponse[] pageItems = restClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/repos/{owner}/{repo}/issues")
                                .queryParam("state", "all")
                                .queryParam("sort", "created")
                                .queryParam("direction", "desc")
                                .queryParam("per_page", PER_PAGE)
                                .queryParam("page", finalPage)
                                .build(owner, repo))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (request, httpResponse) -> {
                            throw new ExternalServiceException("GitHub API returned status "
                                    + httpResponse.getStatusCode().value());
                        })
                        .body(GitHubIssueResponse[].class);

                if (pageItems == null || pageItems.length == 0) {
                    break;
                }

                boolean reachedSavedBoundary = false;

                for (GitHubIssueResponse item : pageItems) {
                    if (item == null || item.createdAt() == null) {
                        continue;
                    }

                    Instant createdAt = item.createdAt().toInstant();
                    if (!createdAt.isAfter(since)) {
                        reachedSavedBoundary = true;
                        continue;
                    }

                    result.add(new LinkUpdateEvent(
                            item.id(),
                            item.pullRequest() == null
                                    ? LinkUpdateType.GITHUB_ISSUE
                                    : LinkUpdateType.GITHUB_PULL_REQUEST,
                            item.title(),
                            item.user() == null ? null : item.user().login(),
                            createdAt,
                            item.body()));
                }

                if (reachedSavedBoundary || pageItems.length < PER_PAGE) {
                    break;
                }

                page++;
            }

            return result.stream()
                    .sorted(Comparator.comparing(LinkUpdateEvent::createdAt))
                    .toList();
        } catch (ExternalServiceException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ExternalServiceException("GitHub API request failed", e);
        } catch (Exception e) {
            throw new ExternalServiceException("GitHub API response handling failed", e);
        }
    }

    private static RestClient buildClient(RestClient.Builder builder, String token) {
        RestClient.Builder clientBuilder = builder.baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", API_VERSION);

        if (token != null && !token.isBlank()) {
            clientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        return clientBuilder.build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitRepository(@JsonProperty("updated_at") OffsetDateTime updatedAt) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubIssueResponse(
            long id,
            String title,
            String body,
            GitHubUserResponse user,
            @JsonProperty("created_at") OffsetDateTime createdAt,
            @JsonProperty("pull_request") GitHubPullRequestMarker pullRequest) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubUserResponse(String login) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GitHubPullRequestMarker(String url) {}
}
