package backend.academy.linktracker.scrapper.application.service.handler;

import backend.academy.linktracker.scrapper.application.port.out.StackOverFlowClient;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackOverflowLinkUpdateHandler implements LinkUpdateHandler {

    private final StackOverFlowClient stackOverFlowClient;

    @Override
    public boolean supports(String host, List<String> pathParts) {
        return "stackoverflow.com".equals(host);
    }

    @Override
    public Instant resolveLastUpdate(String url, List<String> pathParts) {
        if (pathParts.size() < 2 || !"questions".equals(pathParts.get(0))) {
            throw new IllegalArgumentException("Invalid StackOverflow URL: " + url);
        }

        try {
            long questionId = Long.parseLong(pathParts.get(1));
            return stackOverFlowClient.getLastUpdate(questionId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid StackOverflow URL: " + url, e);
        }
    }
}
