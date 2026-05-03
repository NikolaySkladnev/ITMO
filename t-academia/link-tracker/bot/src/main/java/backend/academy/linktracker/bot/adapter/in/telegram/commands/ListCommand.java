package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import backend.academy.linktracker.bot.application.port.in.ListTrackedLinksUseCase;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommand implements Command {

    private static final String START_DESCRIPTION = "показать отслеживаемые ссылки";
    private static final String EMPTY_LIST_MESSAGE = "Список отслеживаемых ссылок пуст.";
    private static final String INTERNAL_ERROR_MESSAGE = "Не удалось получить список ссылок.";

    private final ListTrackedLinksUseCase listTrackedLinksUseCase;
    private final TelegramBot bot;

    @Override
    public String name() {
        return "/list";
    }

    @Override
    public String description() {
        return START_DESCRIPTION;
    }

    @Override
    public void handle(Update update) {
        long chatId = update.message().chat().id();
        String text = update.message().text();
        String tag = extractTag(text);

        try {
            List<TrackedLinkDto> links = listTrackedLinksUseCase.getLinks(chatId, tag);

            if (links.isEmpty()) {
                bot.execute(new SendMessage(chatId, EMPTY_LIST_MESSAGE));
                return;
            }

            bot.execute(new SendMessage(chatId, formatLinks(links, tag)));
        } catch (Exception e) {
            bot.execute(new SendMessage(chatId, INTERNAL_ERROR_MESSAGE));
        }
    }

    private String formatLinks(List<TrackedLinkDto> links, String tag) {
        StringBuilder message = new StringBuilder();

        if (tag != null) {
            message.append("Отслеживаемые ссылки с тегом: ").append(tag).append(System.lineSeparator());
        } else {
            message.append("Отслеживаемые ссылки").append(System.lineSeparator());
        }

        for (TrackedLinkDto link : links) {
            message.append(link.url()).append(System.lineSeparator());
        }

        return message.toString().trim();
    }

    private String extractTag(String text) {
        String[] parts = text.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            return null;
        }
        return parts[1].trim();
    }
}
