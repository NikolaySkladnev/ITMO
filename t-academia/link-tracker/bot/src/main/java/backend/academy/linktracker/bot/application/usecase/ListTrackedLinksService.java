package backend.academy.linktracker.bot.application.usecase;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import backend.academy.linktracker.bot.application.port.in.ListTrackedLinksUseCase;
import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListTrackedLinksService implements ListTrackedLinksUseCase {

    private final ScrapperClient scrapperClient;

    @Override
    public List<TrackedLinkDto> getLinks(long chatId, String tag) {
        List<TrackedLinkDto> links = scrapperClient.getLinks(chatId);

        if (tag == null || tag.isBlank()) {
            return links;
        }

        return links.stream().filter(link -> link.tags().contains(tag)).toList();
    }
}
