package backend.academy.linktracker.scrapper.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoveLinkServiceTest {

    @Mock
    private ChatDataRepository chatDataRepository;

    @InjectMocks
    private RemoveLinkService service;

    @Test
    void shouldRemoveLink() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";
        when(chatDataRepository.hasChat(new Chat(chatId))).thenReturn(true);
        when(chatDataRepository.removeLinkFromChat(new Chat(chatId), new Link(url, List.of())))
                .thenReturn(Optional.of(new Link(url, List.of("work"))));

        LinkResult result = service.removeLink(chatId, url);

        assertThat(result.url()).isEqualTo(url);
        assertThat(result.tags()).containsExactly("work");
    }

    @Test
    void shouldFailWhenChatDoesNotExist() {
        when(chatDataRepository.hasChat(new Chat(1L))).thenReturn(false);

        assertThatThrownBy(() -> service.removeLink(1L, "https://github.com/openai/openai-java"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chat not found");
    }

    @Test
    void shouldFailWhenLinkDoesNotExist() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";
        when(chatDataRepository.hasChat(new Chat(chatId))).thenReturn(true);
        when(chatDataRepository.removeLinkFromChat(new Chat(chatId), new Link(url, List.of())))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeLink(chatId, url))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Link not found");
    }
}
