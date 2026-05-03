package backend.academy.linktracker.scrapper.application.port.out;

import java.time.Instant;

public interface StackOverFlowClient {
    Instant getLastUpdate(long id);
}
