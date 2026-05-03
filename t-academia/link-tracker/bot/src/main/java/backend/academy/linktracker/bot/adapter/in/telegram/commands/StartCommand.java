package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private static final String START_MESSAGE =
            "Добро пожаловать! Используйте /help, чтобы посмотреть доступные команды.";
    private static final String START_DESCRIPTION = "начать работу";

    private final TelegramBot bot;

    @Override
    public String name() {
        return "/start";
    }

    @Override
    public String description() {
        return START_DESCRIPTION;
    }

    @Override
    public void handle(Update update) {
        bot.execute(new SendMessage(update.message().chat().id().longValue(), START_MESSAGE));
    }
}
