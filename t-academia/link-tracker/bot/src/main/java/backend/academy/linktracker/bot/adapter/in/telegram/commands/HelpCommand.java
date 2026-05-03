package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpCommand implements Command {

    private static final String HELP_MESSAGE = """
        Команды:
        /start - начать работу
        /help - показать список команд
        /track - добавить новую ссылку
        /untrack <ссылка> - перестать отслеживать ссылку
        /list [тег] - показать отслеживаемые ссылки
        /cancel - отменить текущий диалог
        """;

    private static final String HELP_DESCRIPTION = "показать список команд";

    private final TelegramBot bot;

    @Override
    public String name() {
        return "/help";
    }

    @Override
    public String description() {
        return HELP_DESCRIPTION;
    }

    @Override
    public void handle(Update update) {
        bot.execute(new SendMessage(update.message().chat().id().longValue(), HELP_MESSAGE.trim()));
    }
}
