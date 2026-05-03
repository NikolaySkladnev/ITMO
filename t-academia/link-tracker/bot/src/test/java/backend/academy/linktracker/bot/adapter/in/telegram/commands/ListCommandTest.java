package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import static backend.academy.linktracker.bot.support.TelegramTestSupport.textOf;
import static backend.academy.linktracker.bot.support.TelegramTestSupport.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import backend.academy.linktracker.bot.application.port.in.ListTrackedLinksUseCase;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListCommandTest {

    @Mock
    private ListTrackedLinksUseCase listTrackedLinksUseCase;

    @Mock
    private TelegramBot bot;

    @InjectMocks
    private ListCommand command;

    @Test
    void shouldSendEmptyMessageWhenNoLinks() {
        when(listTrackedLinksUseCase.getLinks(1L, null)).thenReturn(List.of());

        command.handle(update(1L, "/list"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        assertThat(textOf(captor.getValue())).isEqualTo("Список отслеживаемых ссылок пуст.");
    }

    @Test
    void shouldSendFormattedListWithoutTag() {
        when(listTrackedLinksUseCase.getLinks(1L, null))
                .thenReturn(List.of(
                        new TrackedLinkDto(1L, "https://github.com/openai/openai-java", List.of("work")),
                        new TrackedLinkDto(2L, "https://stackoverflow.com/questions/1/example", List.of("docs"))));

        command.handle(update(1L, "/list"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        assertThat(textOf(captor.getValue()))
                .isEqualTo("Отслеживаемые ссылки\n"
                        + "https://github.com/openai/openai-java\n"
                        + "https://stackoverflow.com/questions/1/example");
    }

    @Test
    void shouldSendFormattedListWithTag() {
        when(listTrackedLinksUseCase.getLinks(1L, "work"))
                .thenReturn(List.of(new TrackedLinkDto(1L, "https://github.com/openai/openai-java", List.of("work"))));

        command.handle(update(1L, "/list work"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        assertThat(textOf(captor.getValue()))
                .isEqualTo("Отслеживаемые ссылки с тегом: work\nhttps://github.com/openai/openai-java");
    }

    @Test
    void shouldSendInternalErrorOnFailure() {
        when(listTrackedLinksUseCase.getLinks(1L, null)).thenThrow(new RuntimeException("boom"));

        command.handle(update(1L, "/list"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        assertThat(textOf(captor.getValue())).isEqualTo("Не удалось получить список ссылок.");
    }
}
