package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import backend.academy.linktracker.bot.application.port.in.StartTrackDialogUseCase;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackCommand implements Command {

    private static final String START_DESCRIPTION = "добавить новую ссылку";
    private static final String START_MESSAGE = "Отправь ссылку, которую нужно отслеживать.";

    private final StartTrackDialogUseCase startTrackDialogUseCase;
    private final TelegramBot bot;

    @Override
    public String name() {
        return "/track";
    }

    @Override
    public String description() {
        return START_DESCRIPTION;
    }

    @Override
    public void handle(Update update) {
        long chatId = update.message().chat().id();
        startTrackDialogUseCase.start(chatId);
        bot.execute(new SendMessage(chatId, START_MESSAGE));
    }
}
