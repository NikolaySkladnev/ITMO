package backend.academy.linktracker.bot.adapter.out.memory;

import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUserStateRepository implements UserStateRepository {

    private final Map<Long, TrackingDialogState> states = new ConcurrentHashMap<>();

    @Override
    public TrackingDialogState getState(long chatId) {
        return states.get(chatId);
    }

    @Override
    public void setState(long chatId, TrackingDialogState status) {
        states.put(chatId, status);
    }

    @Override
    public void clearState(long chatId) {
        states.remove(chatId);
    }
}
