package backend.academy.linktracker.bot.application.port.out;

import java.util.List;

public interface NotificationSender {
    void send(String text, List<Long> chatIds);
}
