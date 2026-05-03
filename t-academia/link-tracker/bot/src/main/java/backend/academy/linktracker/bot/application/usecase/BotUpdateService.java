package backend.academy.linktracker.bot.application.usecase;

import backend.academy.linktracker.bot.application.port.in.ProcessBotUpdateUseCase;
import backend.academy.linktracker.bot.application.port.out.NotificationSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotUpdateService implements ProcessBotUpdateUseCase {

    private final NotificationSender notificationSender;

    @Override
    public void process(long id, String url, String description, List<Long> tgChatIds) {
        String text = buildMessage(url, description);
        notificationSender.send(text, tgChatIds);
    }

    private String buildMessage(String url, String description) {
        if (description == null || description.isBlank()) {
            return "Обнаружено обновление по ссылке: " + url;
        }
        return "Обнаружено обновление по ссылке: " + url + "\n" + description;
    }
}
