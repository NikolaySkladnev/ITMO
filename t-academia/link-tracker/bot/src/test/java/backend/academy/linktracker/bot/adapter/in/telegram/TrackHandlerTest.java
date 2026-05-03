package backend.academy.linktracker.bot.adapter.in.telegram;

import static backend.academy.linktracker.bot.support.TelegramTestSupport.textOf;
import static backend.academy.linktracker.bot.support.TelegramTestSupport.update;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.adapter.in.telegram.dialog.MessageHandler;
import backend.academy.linktracker.bot.adapter.in.telegram.dialog.StateFilter;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackHandlerTest {

    @Mock
    private TelegramBot bot;

    @Mock
    private UserStateRepository userStateRepository;

    @Test
    void shouldIgnoreMessageWhenDialogIsNotActive() {
        when(userStateRepository.getState(1L)).thenReturn(null);

        TrackHandler handler = new TrackHandler(bot, userStateRepository, List.of());

        handler.handle(update(1L, "hello"));

        verify(bot, never()).execute(any(SendMessage.class));
    }

    @Test
    void shouldAskForTagsAndSwitchToWaitTagsState() {
        when(userStateRepository.getState(1L)).thenReturn(TrackingDialogState.WAIT_LINK);

        MessageHandler waitLinkHandler = MessageHandler.builder()
                .withFilter(new StateFilter(TrackingDialogState.WAIT_LINK))
                .nextState(TrackingDialogState.WAIT_TAGS)
                .method(context ->
                        new SendMessage(context.chatId(), "Отправь теги через запятую или нажми «Пропустить»."))
                .invalidInputMessage("Ссылка некорректна. Поддерживаются только GitHub и StackOverflow.")
                .build();

        TrackHandler handler = new TrackHandler(bot, userStateRepository, List.of(waitLinkHandler));

        handler.handle(update(1L, "https://github.com/openai/openai-java"));

        verify(userStateRepository).setState(1L, TrackingDialogState.WAIT_TAGS);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());

        assertThat(textOf(captor.getValue())).isEqualTo("Отправь теги через запятую или нажми «Пропустить».");
    }

    @Test
    void shouldConfirmTrackingAndClearStateAfterTags() {
        when(userStateRepository.getState(1L)).thenReturn(TrackingDialogState.WAIT_TAGS);

        MessageHandler waitTagsHandler = MessageHandler.builder()
                .withFilter(new StateFilter(TrackingDialogState.WAIT_TAGS))
                .method(context -> new SendMessage(context.chatId(), "Ссылка добавлена в отслеживание."))
                .alreadyTrackedMessage("Ссылка уже отслеживается.")
                .build();

        TrackHandler handler = new TrackHandler(bot, userStateRepository, List.of(waitTagsHandler));

        handler.handle(update(1L, "work, docs"));

        verify(userStateRepository).clearState(1L);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());

        assertThat(textOf(captor.getValue())).isEqualTo("Ссылка добавлена в отслеживание.");
    }

    @Test
    void shouldNotifyAboutInvalidLink() {
        when(userStateRepository.getState(1L)).thenReturn(TrackingDialogState.WAIT_LINK);

        MessageHandler waitLinkHandler = MessageHandler.builder()
                .withFilter(new StateFilter(TrackingDialogState.WAIT_LINK))
                .nextState(TrackingDialogState.WAIT_TAGS)
                .method(_ -> {
                    throw new IllegalArgumentException("Invalid link");
                })
                .invalidInputMessage("Ссылка некорректна. Поддерживаются только GitHub и StackOverflow.")
                .build();

        TrackHandler handler = new TrackHandler(bot, userStateRepository, List.of(waitLinkHandler));

        handler.handle(update(1L, "bad"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());

        assertThat(textOf(captor.getValue()))
                .isEqualTo("Ссылка некорректна. Поддерживаются только GitHub и StackOverflow.");
    }

    @Test
    void shouldNotifyWhenLinkAlreadyTracked() {
        when(userStateRepository.getState(1L)).thenReturn(TrackingDialogState.WAIT_TAGS);

        MessageHandler waitTagsHandler = MessageHandler.builder()
                .withFilter(new StateFilter(TrackingDialogState.WAIT_TAGS))
                .method(_ -> {
                    throw new StatusRuntimeException(Status.ALREADY_EXISTS);
                })
                .alreadyTrackedMessage("Ссылка уже отслеживается.")
                .build();

        TrackHandler handler = new TrackHandler(bot, userStateRepository, List.of(waitTagsHandler));

        handler.handle(update(1L, "work"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());

        assertThat(textOf(captor.getValue())).isEqualTo("Ссылка уже отслеживается.");
    }

    @Test
    void shouldSendInternalErrorOnUnexpectedGrpcStatus() {
        when(userStateRepository.getState(1L)).thenReturn(TrackingDialogState.WAIT_TAGS);

        MessageHandler waitTagsHandler = MessageHandler.builder()
                .withFilter(new StateFilter(TrackingDialogState.WAIT_TAGS))
                .method(_ -> {
                    throw new StatusRuntimeException(Status.INTERNAL);
                })
                .alreadyTrackedMessage("Ссылка уже отслеживается.")
                .build();

        TrackHandler handler = new TrackHandler(bot, userStateRepository, List.of(waitTagsHandler));

        handler.handle(update(1L, "work"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot).execute(captor.capture());

        assertThat(textOf(captor.getValue())).isEqualTo("Не удалось обработать запрос.");
    }
}
