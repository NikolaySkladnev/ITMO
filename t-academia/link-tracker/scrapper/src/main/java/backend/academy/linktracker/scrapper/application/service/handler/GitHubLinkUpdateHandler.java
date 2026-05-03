package backend.academy.linktracker.scrapper.application.service.handler;

import backend.academy.linktracker.scrapper.application.port.out.GitHubClient;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitHubLinkUpdateHandler implements LinkUpdateHandler {

    private final GitHubClient gitHubClient;

    @Override
    public boolean supports(String host, List<String> pathParts) {
        return "github.com".equals(host);
    }

    @Override
    public Instant resolveLastUpdate(String url, List<String> pathParts) {
        if (pathParts.size() < 2
                || pathParts.get(0).isBlank()
                || pathParts.get(1).isBlank()) {
            throw new IllegalArgumentException("Invalid GitHub URL: " + url);
        }

        return gitHubClient.getRepository(pathParts.get(0), pathParts.get(1));
    }
}
