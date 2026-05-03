package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import static backend.academy.linktracker.bot.support.TelegramTestSupport.textOf;
import static backend.academy.linktracker.bot.support.TelegramTestSupport.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.application.port.in.UntrackLinkUseCase;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UntrackCommandTest {

    @Mock
    private TelegramBot bot;

    @Mock
    private UntrackLinkUseCase untrackLinkUseCase;

    @InjectMocks
    private UntrackCommand command;

    @Test
    void shouldRejectWhenLinkIsMissing() {
        command.handle(update(1L, "/untrack"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        assertThat(textOf(captor.getValue())).isEqualTo("Использование: /untrack <ссылка>");
    }

    @Test
    void shouldUntrackLink() {
        command.handle(update(1L, "/untrack https://github.com/openai/openai-java"));

        verify(untrackLinkUseCase).untrack(1L, "https://github.com/openai/openai-java");
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        assertThat(textOf(captor.getValue())).isEqualTo("Ссылка удалена из отслеживания.");
    }

    @Test
    void shouldSendErrorWhenUseCaseFails() {
        Mockito.doThrow(new RuntimeException("boom"))
                .when(untrackLinkUseCase)
                .untrack(1L, "https://github.com/openai/openai-java");

        command.handle(update(1L, "/untrack https://github.com/openai/openai-java"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());
        assertThat(textOf(captor.getValue())).isEqualTo("Не получилось удалить ссылку из отслеживания.");
    }
}
