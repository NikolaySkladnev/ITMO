package backend.academy.linktracker.bot.application.port.out;

import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;

public interface UserStateRepository {
    TrackingDialogState getState(long chatId);

    void setState(long chatId, TrackingDialogState status);

    void clearState(long chatId);
}
