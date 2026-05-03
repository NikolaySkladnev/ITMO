package backend.academy.linktracker.scrapper.application.service;

import backend.academy.linktracker.scrapper.application.service.handler.LinkUpdateHandler;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkUpdateResolver {

    private final List<LinkUpdateHandler> handlers;
    private final LinkUrlParser linkUrlParser;

    public Instant resolveLastUpdate(String url) {
        LinkUrlParser.ParsedLink link = linkUrlParser.parseHttps(url);

        return handlers.stream()
                .filter(handler -> handler.supports(link.host(), link.parts()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported URL: " + url))
                .resolveLastUpdate(url, link.parts());
    }
}
