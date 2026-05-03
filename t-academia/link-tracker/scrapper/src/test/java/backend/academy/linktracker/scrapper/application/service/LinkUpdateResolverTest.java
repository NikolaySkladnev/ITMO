package backend.academy.linktracker.scrapper.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.port.out.GitHubClient;
import backend.academy.linktracker.scrapper.application.port.out.StackOverFlowClient;
import backend.academy.linktracker.scrapper.application.service.handler.GitHubLinkUpdateHandler;
import backend.academy.linktracker.scrapper.application.service.handler.LinkUpdateHandler;
import backend.academy.linktracker.scrapper.application.service.handler.StackOverflowLinkUpdateHandler;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkUpdateResolverTest {

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverFlowClient stackOverFlowClient;

    private LinkUpdateResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new LinkUpdateResolver(
                List.<LinkUpdateHandler>of(
                        new GitHubLinkUpdateHandler(gitHubClient),
                        new StackOverflowLinkUpdateHandler(stackOverFlowClient)),
                new LinkUrlParser());
    }

    @Test
    void shouldResolveGithubRepositoryUpdateTime() {
        Instant expected = Instant.parse("2026-03-27T14:00:00Z");
        when(gitHubClient.getRepository("openai", "openai-java")).thenReturn(expected);

        Instant actual = resolver.resolveLastUpdate("https://github.com/openai/openai-java");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldResolveGithubRepositoryUpdateTimeForWwwHost() {
        Instant expected = Instant.parse("2026-03-27T14:00:00Z");
        when(gitHubClient.getRepository("openai", "openai-java")).thenReturn(expected);

        Instant actual = resolver.resolveLastUpdate("https://www.github.com/openai/openai-java");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldResolveStackoverflowQuestionUpdateTime() {
        Instant expected = Instant.parse("2026-03-27T14:00:00Z");
        when(stackOverFlowClient.getLastUpdate(123L)).thenReturn(expected);

        Instant actual = resolver.resolveLastUpdate("https://stackoverflow.com/questions/123/example");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldRejectInvalidGithubUrl() {
        assertThatThrownBy(() -> resolver.resolveLastUpdate("https://github.com/openai"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid GitHub URL");
    }

    @Test
    void shouldRejectInvalidStackoverflowQuestionId() {
        assertThatThrownBy(() -> resolver.resolveLastUpdate("https://stackoverflow.com/questions/not-a-number/example"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid StackOverflow URL");
    }

    @Test
    void shouldRejectUnsupportedHost() {
        assertThatThrownBy(() -> resolver.resolveLastUpdate("https://gitlab.com/openai/openai-java"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported URL");
    }
}
