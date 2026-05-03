package backend.academy.linktracker.scrapper.application.service.handler;

import java.time.Instant;
import java.util.List;

public interface LinkUpdateHandler {
    boolean supports(String host, List<String> pathParts);

    Instant resolveLastUpdate(String url, List<String> pathParts);
}
