package backend.academy.linktracker.scrapper.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.dto.LinkProcessingReport;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.exception.ExternalServiceException;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import backend.academy.linktracker.scrapper.infrastructure.properties.SchedulerProperties;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackedLinkBatchProcessorTest {

    @Mock
    private ChatDataRepository chatDataRepository;

    @Mock
    private LinkUpdateResolver linkUpdateResolver;

    @Mock
    private LinkUpdateQueryService linkUpdateQueryService;

    @Mock
    private LinkNotificationService linkNotificationService;

    private ExecutorService executorService;
    private SchedulerProperties schedulerProperties;
    private TrackedLinkBatchProcessor service;

    @BeforeEach
    void setUp() {
        schedulerProperties = new SchedulerProperties();
        schedulerProperties.setBatchSize(100);
        schedulerProperties.setParallelism(1);

        executorService = Executors.newFixedThreadPool(2);

        service = new TrackedLinkBatchProcessor(
                chatDataRepository,
                linkUpdateResolver,
                linkUpdateQueryService,
                linkNotificationService,
                schedulerProperties,
                executorService);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    void shouldInitializeLastUpdateTimeWhenItIsMissing() {
        String url = "https://github.com/openai/openai-java";
        Link link = new Link(url, List.of());
        Instant current = Instant.parse("2026-03-10T10:15:30Z");

        when(chatDataRepository.getTrackedLinksPage(0, 100)).thenReturn(List.of(url));
        when(chatDataRepository.getLastUpdateTime(link)).thenReturn(null);
        when(linkUpdateResolver.resolveLastUpdate(url)).thenReturn(current);

        LinkProcessingReport report = service.processAll();

        assertThat(report.checkedLinks()).isEqualTo(1);
        assertThat(report.foundUpdates()).isEqualTo(0);
        assertThat(report.failedLinks()).isEmpty();

        verify(chatDataRepository).setLastUpdateTime(link, current);
        verify(linkUpdateQueryService, never()).findUpdates(url, current);
    }

    @Test
    void shouldProcessLinksByConfiguredBatches() {
        schedulerProperties.setBatchSize(2);

        String url1 = "https://github.com/openai/openai-java";
        String url2 = "https://github.com/openai/openai-python";
        String url3 = "https://stackoverflow.com/questions/123/example";

        when(chatDataRepository.getTrackedLinksPage(0, 2)).thenReturn(List.of(url1, url2));
        when(chatDataRepository.getTrackedLinksPage(2, 2)).thenReturn(List.of(url3));

        when(chatDataRepository.getLastUpdateTime(new Link(url1, List.of()))).thenReturn(null);
        when(chatDataRepository.getLastUpdateTime(new Link(url2, List.of()))).thenReturn(null);
        when(chatDataRepository.getLastUpdateTime(new Link(url3, List.of()))).thenReturn(null);

        when(linkUpdateResolver.resolveLastUpdate(url1)).thenReturn(Instant.parse("2026-03-10T10:15:30Z"));
        when(linkUpdateResolver.resolveLastUpdate(url2)).thenReturn(Instant.parse("2026-03-10T10:15:30Z"));
        when(linkUpdateResolver.resolveLastUpdate(url3)).thenReturn(Instant.parse("2026-03-10T10:15:30Z"));

        LinkProcessingReport report = service.processAll();

        assertThat(report.checkedLinks()).isEqualTo(3);
        assertThat(report.foundUpdates()).isEqualTo(0);
        assertThat(report.failedLinks()).isEmpty();

        verify(chatDataRepository).getTrackedLinksPage(0, 2);
        verify(chatDataRepository).getTrackedLinksPage(2, 2);
    }

    @Test
    void shouldSendNotificationsAndUpdateWatermarkWhenUpdatesFound() {
        String url = "https://github.com/openai/openai-java";
        Link link = new Link(url, List.of());
        Instant saved = Instant.parse("2026-03-10T10:15:30Z");

        LinkUpdateEvent first = new LinkUpdateEvent(
                1L,
                LinkUpdateType.GITHUB_ISSUE,
                "Issue title",
                "octocat",
                Instant.parse("2026-03-11T09:00:00Z"),
                "Issue body");

        LinkUpdateEvent second = new LinkUpdateEvent(
                2L,
                LinkUpdateType.GITHUB_PULL_REQUEST,
                "PR title",
                "hubot",
                Instant.parse("2026-03-11T10:00:00Z"),
                "PR body");

        when(chatDataRepository.getTrackedLinksPage(0, 100)).thenReturn(List.of(url));
        when(chatDataRepository.getLastUpdateTime(link)).thenReturn(saved);
        when(linkUpdateQueryService.findUpdates(url, saved)).thenReturn(List.of(first, second));
        when(chatDataRepository.getAllChatByLink(link)).thenReturn(List.of(new Chat(1L), new Chat(2L)));

        LinkProcessingReport report = service.processAll();

        assertThat(report.checkedLinks()).isEqualTo(1);
        assertThat(report.foundUpdates()).isEqualTo(2);
        assertThat(report.failedLinks()).isEmpty();

        verify(linkNotificationService).send(url, List.of(1L, 2L), List.of(first, second));
        verify(chatDataRepository).setLastUpdateTime(link, Instant.parse("2026-03-11T10:00:00Z"));
    }

    @Test
    void shouldUpdateWatermarkWithoutNotificationWhenNoSubscribers() {
        String url = "https://stackoverflow.com/questions/123/example";
        Link link = new Link(url, List.of());
        Instant saved = Instant.parse("2026-03-10T10:15:30Z");

        LinkUpdateEvent event = new LinkUpdateEvent(
                10L,
                LinkUpdateType.STACKOVERFLOW_ANSWER,
                "Question title",
                "Alice",
                Instant.parse("2026-03-11T09:00:00Z"),
                "Answer body");

        when(chatDataRepository.getTrackedLinksPage(0, 100)).thenReturn(List.of(url));
        when(chatDataRepository.getLastUpdateTime(link)).thenReturn(saved);
        when(linkUpdateQueryService.findUpdates(url, saved)).thenReturn(List.of(event));
        when(chatDataRepository.getAllChatByLink(link)).thenReturn(List.of());

        LinkProcessingReport report = service.processAll();

        assertThat(report.checkedLinks()).isEqualTo(1);
        assertThat(report.foundUpdates()).isEqualTo(1);
        assertThat(report.failedLinks()).isEmpty();

        verify(linkNotificationService, never()).send(url, List.of(), List.of(event));
        verify(chatDataRepository).setLastUpdateTime(link, Instant.parse("2026-03-11T09:00:00Z"));
    }

    @Test
    void shouldIsolateFailedLinksAndContinueProcessingOthers() {
        String brokenUrl = "https://github.com/openai/broken";
        String goodUrl = "https://stackoverflow.com/questions/123/example";

        Link brokenLink = new Link(brokenUrl, List.of());
        Link goodLink = new Link(goodUrl, List.of());
        Instant saved = Instant.parse("2026-03-10T10:15:30Z");

        LinkUpdateEvent event = new LinkUpdateEvent(
                20L,
                LinkUpdateType.STACKOVERFLOW_COMMENT,
                "Question title",
                "Bob",
                Instant.parse("2026-03-11T11:00:00Z"),
                "Comment body");

        when(chatDataRepository.getTrackedLinksPage(0, 100)).thenReturn(List.of(brokenUrl, goodUrl));

        when(chatDataRepository.getLastUpdateTime(brokenLink)).thenReturn(saved);
        when(chatDataRepository.getLastUpdateTime(goodLink)).thenReturn(saved);

        when(linkUpdateQueryService.findUpdates(brokenUrl, saved))
                .thenThrow(new ExternalServiceException("external failure"));
        when(linkUpdateQueryService.findUpdates(goodUrl, saved)).thenReturn(List.of(event));

        when(chatDataRepository.getAllChatByLink(goodLink)).thenReturn(List.of(new Chat(7L)));

        LinkProcessingReport report = service.processAll();

        assertThat(report.checkedLinks()).isEqualTo(2);
        assertThat(report.foundUpdates()).isEqualTo(1);
        assertThat(report.failedLinks()).containsExactly(brokenUrl);

        verify(linkNotificationService).send(goodUrl, List.of(7L), List.of(event));
        verify(chatDataRepository).setLastUpdateTime(goodLink, Instant.parse("2026-03-11T11:00:00Z"));
    }
}
