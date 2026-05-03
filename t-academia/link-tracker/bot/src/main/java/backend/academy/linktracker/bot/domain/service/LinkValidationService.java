package backend.academy.linktracker.bot.domain.service;

import backend.academy.linktracker.bot.domain.entities.LinkResourceType;
import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class LinkValidationService {

    public boolean isSupported(String rawUrl) {
        return detectType(rawUrl).isPresent();
    }

    public Optional<LinkResourceType> detectType(String rawUrl) {
        try {
            URI uri = URI.create(rawUrl);

            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                return Optional.empty();
            }

            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                return Optional.empty();
            }

            if (isGithub(host, uri.getPath())) {
                return Optional.of(LinkResourceType.GITHUB);
            }

            if (isStackoverflow(host, uri.getPath())) {
                return Optional.of(LinkResourceType.STACKOVERFLOW);
            }

            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean isGithub(String host, String path) {
        if (!host.equals("github.com") && !host.equals("www.github.com")) {
            return false;
        }

        if (path == null || path.isBlank()) {
            return false;
        }

        String[] parts = path.split("/");
        return parts.length >= 3 && !parts[1].isBlank() && !parts[2].isBlank();
    }

    private boolean isStackoverflow(String host, String path) {
        if (!host.equals("stackoverflow.com") && !host.equals("www.stackoverflow.com")) {
            return false;
        }

        return path != null && path.matches("^/questions/\\d+.*");
    }
}
