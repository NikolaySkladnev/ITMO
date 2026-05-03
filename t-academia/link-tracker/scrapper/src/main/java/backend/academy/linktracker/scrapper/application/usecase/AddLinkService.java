package backend.academy.linktracker.scrapper.application.usecase;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.port.in.AddLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.application.service.LinkUpdateResolver;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLinkService implements AddLinkUseCase {

    private final ChatDataRepository chatDataRepository;
    private final LinkUpdateResolver linkUpdateResolver;

    @Override
    public LinkResult addLink(long chatId, String url, List<String> tags, List<String> filters) {
        Chat chat = new Chat(chatId);
        List<String> safeTags = tags == null ? List.of() : List.copyOf(tags);
        Link link = new Link(url, safeTags);

        if (!chatDataRepository.hasChat(chat)) {
            throw new IllegalArgumentException("Chat not found: " + chatId);
        }

        if (chatDataRepository.findLinkByChat(chat, url).isPresent()) {
            throw new IllegalStateException("Link is already tracked");
        }

        Instant lastUpdated = linkUpdateResolver.resolveLastUpdate(url);

        boolean added = chatDataRepository.addLinkToChat(chat, link);
        if (!added) {
            throw new IllegalStateException("Link is already tracked");
        }

        chatDataRepository.setLastUpdateTime(link, lastUpdated);

        return new LinkResult(linkId(url), url, safeTags, List.of());
    }

    private long linkId(String url) {
        return Integer.toUnsignedLong(url.hashCode());
    }
}
