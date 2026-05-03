package backend.academy.linktracker.scrapper.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.service.handler.LinkEventsHandler;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkUpdateQueryServiceTest {

    @Mock
    private LinkEventsHandler githubHandler;

    @Mock
    private LinkEventsHandler stackoverflowHandler;

    private LinkUpdateQueryService service;

    @BeforeEach
    void setUp() {
        service = new LinkUpdateQueryService(List.of(githubHandler, stackoverflowHandler), new LinkUrlParser());
    }

    @Test
    void shouldDelegateToSupportedHandler() {
        String url = "https://github.com/openai/openai-java";
        Instant since = Instant.parse("2026-03-20T10:00:00Z");
        List<String> pathParts = List.of("openai", "openai-java");

        List<LinkUpdateEvent> expected = List.of(new LinkUpdateEvent(
                1L,
                LinkUpdateType.GITHUB_ISSUE,
                "Issue title",
                "octocat",
                Instant.parse("2026-03-20T10:15:30Z"),
                "body"));

        when(githubHandler.supports(eq("github.com"), eq(pathParts))).thenReturn(true);
        when(githubHandler.findUpdates(eq(url), eq(pathParts), eq(since))).thenReturn(expected);

        List<LinkUpdateEvent> actual = service.findUpdates(url, since);

        assertThat(actual).containsExactlyElementsOf(expected);
        verify(githubHandler).findUpdates(url, pathParts, since);
    }

    @Test
    void shouldNormalizeWwwHostBeforeDelegation() {
        String url = "https://www.github.com/openai/openai-java";
        Instant since = Instant.parse("2026-03-20T10:00:00Z");
        List<String> pathParts = List.of("openai", "openai-java");

        when(githubHandler.supports(eq("github.com"), eq(pathParts))).thenReturn(true);
        when(githubHandler.findUpdates(eq(url), eq(pathParts), eq(since))).thenReturn(List.of());

        assertThat(service.findUpdates(url, since)).isEmpty();

        verify(githubHandler).findUpdates(url, pathParts, since);
    }

    @Test
    void shouldRejectUnsupportedUrl() {
        assertThatThrownBy(() -> service.findUpdates(
                        "https://gitlab.com/openai/openai-java", Instant.parse("2026-03-20T10:00:00Z")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported URL");
    }

    @Test
    void shouldRejectNonHttpsUrl() {
        assertThatThrownBy(() -> service.findUpdates(
                        "http://github.com/openai/openai-java", Instant.parse("2026-03-20T10:00:00Z")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported URL scheme");
    }
}
