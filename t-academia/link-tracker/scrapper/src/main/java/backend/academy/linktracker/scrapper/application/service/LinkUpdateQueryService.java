package backend.academy.linktracker.scrapper.application.service;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.service.handler.LinkEventsHandler;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateQueryService {

    private final List<LinkEventsHandler> handlers;
    private final LinkUrlParser linkUrlParser;

    public List<LinkUpdateEvent> findUpdates(String url, Instant since) {
        LinkUrlParser.ParsedLink link = linkUrlParser.parseHttps(url);

        return handlers.stream()
                .filter(handler -> handler.supports(link.host(), link.parts()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported URL: " + url))
                .findUpdates(url, link.parts(), since);
    }
}
