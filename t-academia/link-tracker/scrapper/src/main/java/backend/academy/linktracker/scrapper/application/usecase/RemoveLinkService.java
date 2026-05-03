package backend.academy.linktracker.scrapper.application.usecase;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.port.in.RemoveLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoveLinkService implements RemoveLinkUseCase {

    private final ChatDataRepository chatDataRepository;

    @Override
    public LinkResult removeLink(long chatId, String url) {
        Chat chat = new Chat(chatId);

        if (!chatDataRepository.hasChat(chat)) {
            throw new IllegalArgumentException("Chat not found: " + chatId);
        }

        Link removed = chatDataRepository
                .removeLinkFromChat(chat, new Link(url, List.of()))
                .orElseThrow(() -> new IllegalArgumentException("Link not found: " + url));

        return new LinkResult(linkId(removed.url()), removed.url(), removed.tags(), List.of());
    }

    private long linkId(String url) {
        return Integer.toUnsignedLong(url.hashCode());
    }
}
