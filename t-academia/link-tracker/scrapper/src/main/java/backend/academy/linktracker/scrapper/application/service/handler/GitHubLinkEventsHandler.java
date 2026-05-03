package backend.academy.linktracker.scrapper.application.service.handler;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.port.out.GitHubEventsClient;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitHubLinkEventsHandler implements LinkEventsHandler {

    private final GitHubEventsClient gitHubEventsClient;

    @Override
    public boolean supports(String host, List<String> pathParts) {
        return "github.com".equals(host);
    }

    @Override
    public List<LinkUpdateEvent> findUpdates(String url, List<String> pathParts, Instant since) {
        if (pathParts.size() < 2
                || pathParts.get(0).isBlank()
                || pathParts.get(1).isBlank()) {
            throw new IllegalArgumentException("Invalid GitHub URL: " + url);
        }

        return gitHubEventsClient.getUpdates(pathParts.get(0), pathParts.get(1), since);
    }
}
