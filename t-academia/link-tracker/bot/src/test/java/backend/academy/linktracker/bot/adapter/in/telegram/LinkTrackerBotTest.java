package backend.academy.linktracker.bot.adapter.in.telegram;

import static backend.academy.linktracker.bot.support.TelegramTestSupport.textOf;
import static backend.academy.linktracker.bot.support.TelegramTestSupport.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.adapter.in.telegram.commands.Command;
import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LinkTrackerBotTest {

    @Mock
    private TelegramBot bot;

    @Mock
    private UserStateRepository userStateRepository;

    @Mock
    private PendingLinkRepository pendingLinkRepository;

    @Mock
    private TrackHandler trackHandler;

    @Mock
    private Command command;

    @Mock
    private Command cancelCommand;

    @Test
    void shouldRegisterMenuAndListenerOnInit() {
        when(command.name()).thenReturn("/list");
        when(command.description()).thenReturn("desc");

        LinkTrackerBot linkTrackerBot =
                new LinkTrackerBot(bot, userStateRepository, pendingLinkRepository, trackHandler, List.of(command));

        ReflectionTestUtils.invokeMethod(linkTrackerBot, "init");

        verify(bot).execute(any(SetMyCommands.class));
        verify(bot).setUpdatesListener(any(UpdatesListener.class));
    }

    @Test
    void shouldSendUnknownCommandMessage() {
        LinkTrackerBot linkTrackerBot =
                new LinkTrackerBot(bot, userStateRepository, pendingLinkRepository, trackHandler, List.of());

        ReflectionTestUtils.invokeMethod(linkTrackerBot, "init");

        ArgumentCaptor<UpdatesListener> captor = ArgumentCaptor.forClass(UpdatesListener.class);
        verify(bot).setUpdatesListener(captor.capture());

        captor.getValue().process(List.of(update(1L, "/unknown")));

        ArgumentCaptor<SendMessage> sendCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(sendCaptor.capture());

        assertThat(textOf(sendCaptor.getValue()))
                .isEqualTo("Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.");
    }

    @Test
    void shouldClearTrackDialogWhenAnotherCommandArrives() {
        when(command.name()).thenReturn("/list");
        when(command.description()).thenReturn("desc");
        when(userStateRepository.getState(1L)).thenReturn(TrackingDialogState.WAIT_TAGS);

        LinkTrackerBot linkTrackerBot =
                new LinkTrackerBot(bot, userStateRepository, pendingLinkRepository, trackHandler, List.of(command));

        ReflectionTestUtils.invokeMethod(linkTrackerBot, "init");

        ArgumentCaptor<UpdatesListener> captor = ArgumentCaptor.forClass(UpdatesListener.class);
        verify(bot).setUpdatesListener(captor.capture());

        captor.getValue().process(List.of(update(1L, "/list")));

        verify(userStateRepository).clearState(1L);
        verify(pendingLinkRepository).clear(1L);
        verify(command).handle(any());
    }

    @Test
    void shouldNotPreClearDialogForCancelCommand() {
        when(cancelCommand.name()).thenReturn("/cancel");
        when(cancelCommand.description()).thenReturn("cancel");

        LinkTrackerBot linkTrackerBot = new LinkTrackerBot(
                bot, userStateRepository, pendingLinkRepository, trackHandler, List.of(cancelCommand));

        ReflectionTestUtils.invokeMethod(linkTrackerBot, "init");

        ArgumentCaptor<UpdatesListener> captor = ArgumentCaptor.forClass(UpdatesListener.class);
        verify(bot).setUpdatesListener(captor.capture());

        captor.getValue().process(List.of(update(1L, "/cancel")));

        verify(userStateRepository, never()).clearState(1L);
        verify(pendingLinkRepository, never()).clear(1L);
        verify(cancelCommand).handle(any());
    }

    @Test
    void shouldDelegatePlainTextToTrackHandler() {
        LinkTrackerBot linkTrackerBot =
                new LinkTrackerBot(bot, userStateRepository, pendingLinkRepository, trackHandler, List.of());

        ReflectionTestUtils.invokeMethod(linkTrackerBot, "init");

        ArgumentCaptor<UpdatesListener> captor = ArgumentCaptor.forClass(UpdatesListener.class);
        verify(bot).setUpdatesListener(captor.capture());

        captor.getValue().process(List.of(update(1L, "https://github.com/openai/openai-java")));

        verify(trackHandler).handle(any());
    }
}
