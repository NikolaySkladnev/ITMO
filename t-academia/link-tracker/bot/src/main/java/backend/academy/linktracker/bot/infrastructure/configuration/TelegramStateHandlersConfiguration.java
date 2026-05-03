package backend.academy.linktracker.bot.infrastructure.configuration;

import backend.academy.linktracker.bot.adapter.in.telegram.dialog.MessageHandler;
import backend.academy.linktracker.bot.adapter.in.telegram.dialog.StateFilter;
import backend.academy.linktracker.bot.application.usecase.TrackDialogService;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TelegramStateHandlersConfiguration {

    private static final String INVALID_LINK_MESSAGE =
            "Ссылка некорректна. Поддерживаются только GitHub и StackOverflow.";
    private static final String ASK_TAGS_MESSAGE = "Отправь теги через запятую или нажми 'Пропустить'";
    private static final String TRACK_SUCCESS_MESSAGE = "Ссылка добавлена в отслеживание.";
    private static final String ALREADY_TRACKED_MESSAGE = "Ссылка уже отслеживается.";
    private static final String SKIP_TAGS_TEXT = "Пропустить";

    private final TrackDialogService trackDialogService;

    @Bean
    public MessageHandler waitingForLinkHandler() {
        return MessageHandler.builder()
                .withFilter(new StateFilter(TrackingDialogState.WAIT_LINK))
                .nextState(TrackingDialogState.WAIT_TAGS)
                .method(handlerContext -> {
                    trackDialogService.handleWaitLink(handlerContext.chatId(), handlerContext.text());
                    return new SendMessage(handlerContext.chatId(), ASK_TAGS_MESSAGE)
                            .replyMarkup(new ReplyKeyboardMarkup(SKIP_TAGS_TEXT)
                                    .oneTimeKeyboard(true)
                                    .resizeKeyboard(true));
                })
                .invalidInputMessage(INVALID_LINK_MESSAGE)
                .build();
    }

    @Bean
    public MessageHandler waitingForTagsHandler() {
        return MessageHandler.builder()
                .withFilter(new StateFilter(TrackingDialogState.WAIT_TAGS))
                .method(handlerContext -> {
                    trackDialogService.handleWaitTags(handlerContext.chatId(), handlerContext.text());
                    return new SendMessage(handlerContext.chatId(), TRACK_SUCCESS_MESSAGE);
                })
                .alreadyTrackedMessage(ALREADY_TRACKED_MESSAGE)
                .build();
    }
}
