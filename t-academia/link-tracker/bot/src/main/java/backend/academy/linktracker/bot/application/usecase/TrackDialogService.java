package backend.academy.linktracker.bot.application.usecase;

import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import backend.academy.linktracker.bot.domain.entities.Link;
import backend.academy.linktracker.bot.domain.service.LinkValidationService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackDialogService {

    private static final String SKIP_TAGS_TEXT = "Пропустить";

    private final PendingLinkRepository pendingLinkRepository;
    private final ScrapperClient scrapperClient;
    private final LinkValidationService linkValidationService;

    public void handleWaitLink(long chatId, String text) {
        String normalizedText = normalize(text);

        if (!linkValidationService.isSupported(normalizedText)) {
            throw new IllegalArgumentException("Invalid link");
        }

        pendingLinkRepository.save(chatId, Link.pending(normalizedText));
    }

    public void handleWaitTags(long chatId, String text) {
        Link pendingLink = pendingLinkRepository.find(chatId);
        if (pendingLink == null) {
            throw new IllegalStateException("Dialog state lost");
        }

        List<String> tags = parseCommaSeparated(text);

        scrapperClient.registerChat(chatId);
        scrapperClient.addLink(chatId, pendingLink.url(), tags);

        pendingLinkRepository.clear(chatId);
    }

    private String normalize(String text) {
        return text == null ? "" : text.strip();
    }

    private List<String> parseCommaSeparated(String text) {
        String normalizedText = normalize(text);

        if (normalizedText.isBlank() || SKIP_TAGS_TEXT.equalsIgnoreCase(normalizedText)) {
            return List.of();
        }

        return Arrays.stream(normalizedText.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .toList();
    }
}
