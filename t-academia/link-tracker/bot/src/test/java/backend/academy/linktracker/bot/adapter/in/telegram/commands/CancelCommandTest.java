package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import static backend.academy.linktracker.bot.support.TelegramTestSupport.textOf;
import static backend.academy.linktracker.bot.support.TelegramTestSupport.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancelCommandTest {

    @Mock
    private UserStateRepository userStateRepository;

    @Mock
    private PendingLinkRepository pendingLinkRepository;

    @Mock
    private TelegramBot bot;

    @InjectMocks
    private CancelCommand command;

    @Test
    void shouldNotifyWhenNoActiveDialog() {
        when(userStateRepository.getState(100L)).thenReturn(null);

        command.handle(update(100L, "/cancel"));

        verify(userStateRepository, never()).clearState(100L);
        verify(pendingLinkRepository, never()).clear(100L);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());

        assertThat(textOf(captor.getValue())).isEqualTo("Нет активного действия для отмены.");
    }

    @Test
    void shouldCancelActiveDialog() {
        when(userStateRepository.getState(100L)).thenReturn(TrackingDialogState.WAIT_LINK);

        command.handle(update(100L, "/cancel"));

        verify(userStateRepository).clearState(100L);
        verify(pendingLinkRepository).clear(100L);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());

        assertThat(textOf(captor.getValue())).isEqualTo("Действие отменено.");
    }
}
