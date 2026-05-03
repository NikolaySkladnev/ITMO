package backend.academy.linktracker.scrapper.application.service.handler;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.port.out.StackOverflowEventsClient;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackOverflowLinkEventsHandler implements LinkEventsHandler {

    private final StackOverflowEventsClient stackOverflowEventsClient;

    @Override
    public boolean supports(String host, List<String> pathParts) {
        return "stackoverflow.com".equals(host);
    }

    @Override
    public List<LinkUpdateEvent> findUpdates(String url, List<String> pathParts, Instant since) {
        if (pathParts.size() < 2 || !"questions".equals(pathParts.get(0))) {
            throw new IllegalArgumentException("Invalid StackOverflow URL: " + url);
        }

        try {
            long questionId = Long.parseLong(pathParts.get(1));
            return stackOverflowEventsClient.getUpdates(questionId, since);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid StackOverflow URL: " + url, e);
        }
    }
}
