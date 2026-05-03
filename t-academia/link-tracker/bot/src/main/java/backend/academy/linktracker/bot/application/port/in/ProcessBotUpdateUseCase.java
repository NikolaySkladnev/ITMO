package backend.academy.linktracker.bot.application.port.in;

import java.util.List;

public interface ProcessBotUpdateUseCase {
    void process(long id, String url, String description, List<Long> tgChatIds);
}
