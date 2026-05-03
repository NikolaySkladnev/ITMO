package backend.academy.linktracker.bot.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import backend.academy.linktracker.bot.domain.entities.Link;
import backend.academy.linktracker.bot.domain.service.LinkValidationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackDialogServiceTest {

    @Mock
    private PendingLinkRepository pendingLinkRepository;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private LinkValidationService linkValidationService;

    @InjectMocks
    private TrackDialogService service;

    @Test
    void shouldSavePendingLinkWhenValidLinkReceived() {
        String url = "https://github.com/openai/openai-java";
        when(linkValidationService.isSupported(url)).thenReturn(true);

        service.handleWaitLink(1L, url);

        verify(pendingLinkRepository).save(1L, Link.pending(url));
    }

    @Test
    void shouldThrowOnInvalidLink() {
        when(linkValidationService.isSupported("bad-link")).thenReturn(false);

        assertThatThrownBy(() -> service.handleWaitLink(1L, "bad-link"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid link");

        verify(pendingLinkRepository, never()).save(anyLong(), any(Link.class));
    }

    @Test
    void shouldRegisterChatAndAddLinkWithParsedTags() {
        String url = "https://github.com/openai/openai-java";
        when(pendingLinkRepository.find(5L)).thenReturn(Link.pending(url));

        service.handleWaitTags(5L, " work, java , docs ");

        verify(scrapperClient).registerChat(5L);
        verify(scrapperClient).addLink(5L, url, List.of("work", "java", "docs"));
        verify(pendingLinkRepository).clear(5L);
    }

    @Test
    void shouldAllowSkippingTagsWithButtonText() {
        String url = "https://stackoverflow.com/questions/123/example";
        when(pendingLinkRepository.find(5L)).thenReturn(Link.pending(url));

        service.handleWaitTags(5L, "Пропустить");

        verify(scrapperClient).registerChat(5L);
        verify(scrapperClient).addLink(5L, url, List.of());
        verify(pendingLinkRepository).clear(5L);
    }

    @Test
    void shouldFailWhenPendingLinkIsLost() {
        when(pendingLinkRepository.find(5L)).thenReturn(null);

        assertThatThrownBy(() -> service.handleWaitTags(5L, "work"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Dialog state lost");

        verify(scrapperClient, never()).registerChat(anyLong());
        verify(scrapperClient, never()).addLink(anyLong(), anyString(), anyList());
        verify(pendingLinkRepository, never()).clear(5L);
    }
}
