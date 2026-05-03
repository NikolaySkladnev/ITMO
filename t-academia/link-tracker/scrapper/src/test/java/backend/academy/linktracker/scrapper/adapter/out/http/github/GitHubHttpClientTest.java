package backend.academy.linktracker.scrapper.adapter.out.http.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.exception.ExternalServiceException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class GitHubHttpClientTest {

    private RestClient.Builder builder;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
    }

    @Test
    void shouldReturnUpdatedAtFromGithubApi() {
        GitHubHttpClient client = new GitHubHttpClient(builder);

        server.expect(requestTo("https://api.github.com/repos/openai/openai-java"))
                .andRespond(withSuccess("""
                        {
                          "updated_at": "2026-03-01T10:15:30Z"
                        }
                        """, APPLICATION_JSON));

        Instant result = client.getRepository("openai", "openai-java");

        assertThat(result).isEqualTo(Instant.parse("2026-03-01T10:15:30Z"));
    }

    @Test
    void shouldReturnNewIssuesAndPullRequestsCreatedAfterTimestamp() {
        GitHubHttpClient client = new GitHubHttpClient(builder);
        Instant since = Instant.parse("2026-03-01T10:00:00Z");

        server.expect(once(), requestTo(startsWith("https://api.github.com/repos/openai/openai-java/issues?")))
                .andRespond(withSuccess("""
                        [
                          {
                            "id": 101,
                            "title": "Issue title",
                            "body": "Issue body",
                            "created_at": "2026-03-01T10:15:30Z",
                            "user": {
                              "login": "octocat"
                            }
                          },
                          {
                            "id": 102,
                            "title": "PR title",
                            "body": "PR body",
                            "created_at": "2026-03-01T11:15:30Z",
                            "user": {
                              "login": "hubot"
                            },
                            "pull_request": {
                              "url": "https://api.github.com/repos/openai/openai-java/pulls/7"
                            }
                          }
                        ]
                        """, APPLICATION_JSON));

        List<LinkUpdateEvent> result = client.getUpdates("openai", "openai-java", since);

        assertThat(result)
                .extracting(LinkUpdateEvent::type, LinkUpdateEvent::title, LinkUpdateEvent::username)
                .containsExactly(
                        tuple(LinkUpdateType.GITHUB_ISSUE, "Issue title", "octocat"),
                        tuple(LinkUpdateType.GITHUB_PULL_REQUEST, "PR title", "hubot"));
    }

    @Test
    void shouldIgnoreItemsCreatedBeforeOrAtSavedTimestamp() {
        GitHubHttpClient client = new GitHubHttpClient(builder);
        Instant since = Instant.parse("2026-03-01T10:15:30Z");

        server.expect(once(), requestTo(startsWith("https://api.github.com/repos/openai/openai-java/issues?")))
                .andRespond(withSuccess("""
                        [
                          {
                            "id": 201,
                            "title": "Old issue",
                            "body": "Old body",
                            "created_at": "2026-03-01T10:15:30Z",
                            "user": {
                              "login": "old-user"
                            }
                          },
                          {
                            "id": 202,
                            "title": "New issue",
                            "body": "New body",
                            "created_at": "2026-03-01T10:15:31Z",
                            "user": {
                              "login": "new-user"
                            }
                          }
                        ]
                        """, APPLICATION_JSON));

        List<LinkUpdateEvent> result = client.getUpdates("openai", "openai-java", since);

        assertThat(result).extracting(LinkUpdateEvent::title).containsExactly("New issue");
    }

    @Test
    void shouldThrowWhenGithubReturnsErrorStatus() {
        GitHubHttpClient client = new GitHubHttpClient(builder);

        server.expect(requestTo("https://api.github.com/repos/openai/openai-java"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getRepository("openai", "openai-java"))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("GitHub API returned status");
    }

    @Test
    void shouldThrowWhenGithubReturnsInvalidBody() {
        GitHubHttpClient client = new GitHubHttpClient(builder);

        server.expect(requestTo("https://api.github.com/repos/openai/openai-java"))
                .andRespond(withSuccess("{}", APPLICATION_JSON));

        assertThatThrownBy(() -> client.getRepository("openai", "openai-java"))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("invalid body");
    }
}
