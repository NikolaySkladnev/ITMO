package backend.academy.linktracker.scrapper.application.port.out;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import java.time.Instant;
import java.util.List;

public interface GitHubEventsClient {
    List<LinkUpdateEvent> getUpdates(String owner, String repo, Instant since);
}
