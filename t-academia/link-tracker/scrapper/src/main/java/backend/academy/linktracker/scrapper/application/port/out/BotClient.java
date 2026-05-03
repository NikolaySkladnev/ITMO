package backend.academy.linktracker.scrapper.application.port.out;

import backend.academy.linktracker.scrapper.application.dto.UpdateNotification;

public interface BotClient {
    void sendUpdate(UpdateNotification update);
}
