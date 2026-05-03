package backend.academy.linktracker.scrapper.application.service.handler;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import java.time.Instant;
import java.util.List;

public interface LinkEventsHandler {
    boolean supports(String host, List<String> pathParts);

    List<LinkUpdateEvent> findUpdates(String url, List<String> pathParts, Instant since);
}
