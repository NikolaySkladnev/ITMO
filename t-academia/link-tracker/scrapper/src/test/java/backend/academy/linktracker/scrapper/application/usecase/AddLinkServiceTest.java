package backend.academy.linktracker.scrapper.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.application.service.LinkUpdateResolver;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddLinkServiceTest {

    @Mock
    private ChatDataRepository chatDataRepository;

    @Mock
    private LinkUpdateResolver linkUpdateResolver;

    @InjectMocks
    private AddLinkService service;

    @Test
    void shouldAddLinkAndPersistLastUpdateTime() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";
        Link link = new Link(url, List.of("work"));
        Instant lastUpdated = Instant.parse("2026-03-01T10:15:30Z");

        when(chatDataRepository.hasChat(new Chat(chatId))).thenReturn(true);
        when(chatDataRepository.findLinkByChat(new Chat(chatId), url)).thenReturn(Optional.empty());
        when(linkUpdateResolver.resolveLastUpdate(url)).thenReturn(lastUpdated);
        when(chatDataRepository.addLinkToChat(new Chat(chatId), link)).thenReturn(true);

        LinkResult result = service.addLink(chatId, url, List.of("work"), List.of("ignored"));

        assertThat(result.url()).isEqualTo(url);
        assertThat(result.tags()).containsExactly("work");
        verify(chatDataRepository).setLastUpdateTime(link, lastUpdated);
    }

    @Test
    void shouldUseEmptyTagsWhenNullProvided() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";
        Link link = new Link(url, List.of());
        Instant lastUpdated = Instant.parse("2026-03-01T10:15:30Z");

        when(chatDataRepository.hasChat(new Chat(chatId))).thenReturn(true);
        when(chatDataRepository.findLinkByChat(new Chat(chatId), url)).thenReturn(Optional.empty());
        when(linkUpdateResolver.resolveLastUpdate(url)).thenReturn(lastUpdated);
        when(chatDataRepository.addLinkToChat(new Chat(chatId), link)).thenReturn(true);

        LinkResult result = service.addLink(chatId, url, null, null);

        assertThat(result.tags()).isEmpty();
        verify(chatDataRepository).setLastUpdateTime(link, lastUpdated);
    }

    @Test
    void shouldFailWhenChatDoesNotExist() {
        when(chatDataRepository.hasChat(new Chat(1L))).thenReturn(false);

        assertThatThrownBy(() -> service.addLink(1L, "https://github.com/openai/openai-java", List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chat not found");
    }

    @Test
    void shouldFailWhenLinkAlreadyTracked() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";
        when(chatDataRepository.hasChat(new Chat(chatId))).thenReturn(true);
        when(chatDataRepository.findLinkByChat(new Chat(chatId), url))
                .thenReturn(Optional.of(new Link(url, List.of())));

        assertThatThrownBy(() -> service.addLink(chatId, url, List.of(), List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already tracked");
    }

    @Test
    void shouldFailWhenRepositoryRejectsDuplicate() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";
        Link link = new Link(url, List.of("work"));
        when(chatDataRepository.hasChat(new Chat(chatId))).thenReturn(true);
        when(chatDataRepository.findLinkByChat(new Chat(chatId), url)).thenReturn(Optional.empty());
        when(linkUpdateResolver.resolveLastUpdate(url)).thenReturn(Instant.parse("2026-03-01T10:15:30Z"));
        when(chatDataRepository.addLinkToChat(new Chat(chatId), link)).thenReturn(false);

        assertThatThrownBy(() -> service.addLink(chatId, url, List.of("work"), List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already tracked");
    }
}
