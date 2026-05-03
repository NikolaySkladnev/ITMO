package backend.academy.linktracker.bot.application.usecase;

import backend.academy.linktracker.bot.application.port.in.UntrackLinkUseCase;
import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UntrackLinkService implements UntrackLinkUseCase {

    private final ScrapperClient scrapperClient;

    @Override
    public void untrack(long chatId, String url) {
        scrapperClient.removeLink(chatId, url);
    }
}
