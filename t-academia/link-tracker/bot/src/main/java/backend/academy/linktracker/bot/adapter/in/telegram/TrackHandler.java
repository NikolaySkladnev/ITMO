package backend.academy.linktracker.bot.adapter.in.telegram;

import backend.academy.linktracker.bot.adapter.in.telegram.dialog.MessageHandler;
import backend.academy.linktracker.bot.adapter.in.telegram.dialog.MessageHandlerContext;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackHandler {

    private static final String INTERNAL_ERROR_MESSAGE = "Не удалось обработать запрос.";

    private final TelegramBot bot;
    private final UserStateRepository userStateRepository;
    private final List<MessageHandler> handlers;

    public void handle(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

        long chatId = update.message().chat().id();
        String text = update.message().text();

        TrackingDialogState currentState = userStateRepository.getState(chatId);
        if (currentState == null) {
            return;
        }

        MessageHandlerContext context = new MessageHandlerContext(chatId, text, update, currentState);

        MessageHandler handler = StreamEx.of(handlers)
                .filter(candidate -> candidate.matches(context))
                .findFirst()
                .orElse(null);

        if (handler == null) {
            bot.execute(new SendMessage(chatId, INTERNAL_ERROR_MESSAGE));
            return;
        }

        try {
            SendMessage response = handler.getMethod().apply(context);

            if (handler.getNextState() == null) {
                userStateRepository.clearState(chatId);
            } else {
                userStateRepository.setState(chatId, handler.getNextState());
            }

            bot.execute(response);
        } catch (IllegalArgumentException e) {
            bot.execute(new SendMessage(chatId, getErrorMessage(handler.getInvalidInputMessage())));
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                bot.execute(new SendMessage(chatId, getErrorMessage(handler.getAlreadyTrackedMessage())));
            } else {
                bot.execute(new SendMessage(chatId, INTERNAL_ERROR_MESSAGE));
            }
        } catch (Exception e) {
            bot.execute(new SendMessage(chatId, INTERNAL_ERROR_MESSAGE));
        }
    }

    private String getErrorMessage(String handlerMessage) {
        return handlerMessage == null || handlerMessage.isBlank() ? INTERNAL_ERROR_MESSAGE : handlerMessage;
    }
}
