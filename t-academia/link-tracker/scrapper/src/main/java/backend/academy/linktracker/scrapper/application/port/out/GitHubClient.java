package backend.academy.linktracker.scrapper.application.port.out;

import java.time.Instant;

public interface GitHubClient {
    Instant getRepository(String owner, String repo);
}
