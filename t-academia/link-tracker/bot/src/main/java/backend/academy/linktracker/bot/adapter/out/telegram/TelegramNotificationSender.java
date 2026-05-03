package backend.academy.linktracker.bot.adapter.out.telegram;

import backend.academy.linktracker.bot.application.port.out.NotificationSender;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramNotificationSender implements NotificationSender {

    private final TelegramBot bot;

    @Override
    public void send(String text, List<Long> chatIds) {
        for (Long chatId : chatIds) {
            bot.execute(new SendMessage(chatId.longValue(), text));
        }
    }
}
