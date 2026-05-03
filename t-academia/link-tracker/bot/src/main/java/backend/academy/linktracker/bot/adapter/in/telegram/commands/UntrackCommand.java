package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import backend.academy.linktracker.bot.application.port.in.UntrackLinkUseCase;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UntrackCommand implements Command {

    private static final String ERROR_MESSAGE = "Не получилось удалить ссылку из отслеживания.";
    private static final String START_DESCRIPTION = "удалить ссылку из отслеживания";
    private static final String UNTRACKED_LINK = "Ссылка удалена из отслеживания.";
    private static final String INVALID_LINK = "Использование: /untrack <ссылка>";

    private final TelegramBot bot;
    private final UntrackLinkUseCase untrackLinkUseCase;

    @Override
    public String name() {
        return "/untrack";
    }

    @Override
    public String description() {
        return START_DESCRIPTION;
    }

    @Override
    public void handle(Update update) {
        long chatId = update.message().chat().id();
        String text = update.message().text();
        String link = extractLink(text);

        if (link == null || link.isBlank()) {
            bot.execute(new SendMessage(chatId, INVALID_LINK));
            return;
        }

        try {
            untrackLinkUseCase.untrack(chatId, link);
            bot.execute(new SendMessage(chatId, UNTRACKED_LINK));
        } catch (Exception e) {
            bot.execute(new SendMessage(chatId, ERROR_MESSAGE));
        }
    }

    private String extractLink(String text) {
        String[] parts = text.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            return null;
        }
        return parts[1].trim();
    }
}
