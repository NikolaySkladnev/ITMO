package backend.academy.linktracker.scrapper.application.usecase;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;
import backend.academy.linktracker.scrapper.application.port.in.GetAllLinksUseCase;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllLinksService implements GetAllLinksUseCase {

    private final ChatDataRepository chatDataRepository;

    @Override
    public ListLinkResult getAllLinks(long chatId) {
        Chat chat = new Chat(chatId);

        if (!chatDataRepository.hasChat(chat)) {
            throw new IllegalArgumentException("Chat not found: " + chatId);
        }

        List<Link> links = chatDataRepository.getAllLinksByChat(chat);

        List<LinkResult> result = links.stream()
                .map(link -> new LinkResult(linkId(link.url()), link.url(), link.tags(), List.of()))
                .toList();

        return new ListLinkResult(result, result.size());
    }

    private long linkId(String url) {
        return Integer.toUnsignedLong(url.hashCode());
    }
}
