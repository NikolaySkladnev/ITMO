package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelCommand implements Command {

    private static final String DESCRIPTION = "отменить текущий диалог";
    private static final String NO_ACTIVE_DIALOG_MESSAGE = "Нет активного действия для отмены.";
    private static final String CANCELLED_MESSAGE = "Действие отменено.";

    private final UserStateRepository userStateRepository;
    private final PendingLinkRepository pendingLinkRepository;
    private final TelegramBot bot;

    @Override
    public String name() {
        return "/cancel";
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public void handle(Update update) {
        long chatId = update.message().chat().id();
        TrackingDialogState currentState = userStateRepository.getState(chatId);

        if (currentState == null) {
            bot.execute(new SendMessage(chatId, NO_ACTIVE_DIALOG_MESSAGE));
            return;
        }

        userStateRepository.clearState(chatId);
        pendingLinkRepository.clear(chatId);

        bot.execute(new SendMessage(chatId, CANCELLED_MESSAGE));
    }
}
