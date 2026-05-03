package backend.academy.linktracker.bot.application.usecase;

import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UntrackLinkServiceTest {

    @Mock
    private ScrapperClient scrapperClient;

    @InjectMocks
    private UntrackLinkService service;

    @Test
    void shouldDelegateRemoveLinkToScrapperClient() {
        service.untrack(7L, "https://github.com/openai/openai-java");

        verify(scrapperClient).removeLink(7L, "https://github.com/openai/openai-java");
    }
}
