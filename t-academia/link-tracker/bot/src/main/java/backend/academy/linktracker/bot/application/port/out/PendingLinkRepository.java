package backend.academy.linktracker.bot.application.port.out;

import backend.academy.linktracker.bot.domain.entities.Link;

public interface PendingLinkRepository {
    void save(long chatId, Link link);

    Link find(long chatId);

    void clear(long chatId);
}
