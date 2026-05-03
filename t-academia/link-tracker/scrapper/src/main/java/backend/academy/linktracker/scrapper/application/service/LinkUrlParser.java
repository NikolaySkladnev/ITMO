package backend.academy.linktracker.scrapper.application.service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LinkUrlParser {

    public ParsedLink parseHttps(String url) {
        URI uri = toUri(url);

        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("Unsupported URL scheme: " + url);
        }

        return new ParsedLink(normalizeHost(uri.getHost()), pathParts(uri));
    }

    public record ParsedLink(String host, List<String> parts) {}

    private URI toUri(String url) {
        try {
            return URI.create(url);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    private String normalizeHost(String host) {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("URL host is missing");
        }

        return host.startsWith("www.") ? host.substring(4) : host;
    }

    private List<String> pathParts(URI uri) {
        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            return List.of();
        }

        return Arrays.stream(path.replaceFirst("^/+", "").split("/"))
                .filter(part -> !part.isBlank())
                .toList();
    }
}
