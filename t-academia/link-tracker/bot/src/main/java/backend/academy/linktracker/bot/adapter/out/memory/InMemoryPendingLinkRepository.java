package backend.academy.linktracker.bot.adapter.out.memory;

import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.domain.entities.Link;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPendingLinkRepository implements PendingLinkRepository {
    private final Map<Long, Link> storage = new ConcurrentHashMap<>();

    @Override
    public void save(long chatId, Link link) {
        storage.put(chatId, link);
    }

    @Override
    public Link find(long chatId) {
        return storage.get(chatId);
    }

    @Override
    public void clear(long chatId) {
        storage.remove(chatId);
    }
}
