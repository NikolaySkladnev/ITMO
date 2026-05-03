package backend.academy.linktracker.scrapper.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.dto.UpdateNotification;
import backend.academy.linktracker.scrapper.application.port.out.BotClient;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkNotificationServiceTest {

    @Mock
    private BotClient botClient;

    @InjectMocks
    private LinkNotificationService service;

    @Test
    void shouldFormatGithubIssueNotificationAndCropPreviewTo200Chars() {
        String body = "a".repeat(250);
        LinkUpdateEvent event = new LinkUpdateEvent(
                101L,
                LinkUpdateType.GITHUB_ISSUE,
                "Add endpoint",
                "octocat",
                Instant.parse("2026-03-20T10:15:30Z"),
                body);

        service.send("https://github.com/openai/openai-java", List.of(1L, 2L), List.of(event));

        ArgumentCaptor<UpdateNotification> captor = ArgumentCaptor.forClass(UpdateNotification.class);
        verify(botClient).sendUpdate(captor.capture());

        UpdateNotification notification = captor.getValue();

        assertThat(notification.url()).isEqualTo("https://github.com/openai/openai-java");
        assertThat(notification.chatIds()).containsExactly(1L, 2L);
        assertThat(notification.description())
                .isEqualTo("""
                        GitHub: новый Issue
                        Название: Add endpoint
                        Автор: octocat
                        Создано: 2026-03-20 10:15:30 UTC
                        Превью описания: %s
                        """.formatted("a".repeat(200)).trim());
    }

    @Test
    void shouldFormatStackoverflowAnswerNotificationAndNormalizeHtmlPreview() {
        LinkUpdateEvent event = new LinkUpdateEvent(
                202L,
                LinkUpdateType.STACKOVERFLOW_ANSWER,
                "How to test?",
                "Alice",
                Instant.parse("2026-03-21T09:30:00Z"),
                "<p>Hello <b>world</b> &amp; everyone</p>\n<p>Line 2</p>");

        service.send("https://stackoverflow.com/questions/123/example", List.of(7L), List.of(event));

        ArgumentCaptor<UpdateNotification> captor = ArgumentCaptor.forClass(UpdateNotification.class);
        verify(botClient).sendUpdate(captor.capture());

        UpdateNotification notification = captor.getValue();

        assertThat(notification.url()).isEqualTo("https://stackoverflow.com/questions/123/example");
        assertThat(notification.chatIds()).containsExactly(7L);
        assertThat(notification.description()).isEqualTo("""
                        StackOverflow: новый ответ
                        Тема: How to test?
                        Автор: Alice
                        Создано: 2026-03-21 09:30:00 UTC
                        Превью: Hello world & everyone Line 2
                        """.trim());
    }

    @Test
    void shouldUseDashWhenEventFieldsAreBlank() {
        LinkUpdateEvent event = new LinkUpdateEvent(
                303L, LinkUpdateType.STACKOVERFLOW_COMMENT, "   ", "", Instant.parse("2026-03-22T08:00:00Z"), "   ");

        service.send("https://stackoverflow.com/questions/123/example", List.of(99L), List.of(event));

        ArgumentCaptor<UpdateNotification> captor = ArgumentCaptor.forClass(UpdateNotification.class);
        verify(botClient).sendUpdate(captor.capture());

        assertThat(captor.getValue().description()).isEqualTo("""
                        StackOverflow: новый комментарий
                        Тема: -
                        Автор: -
                        Создано: 2026-03-22 08:00:00 UTC
                        Превью: -
                        """.trim());
    }
}
