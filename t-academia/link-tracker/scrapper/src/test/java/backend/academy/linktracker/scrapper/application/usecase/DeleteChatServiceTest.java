package backend.academy.linktracker.scrapper.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteChatServiceTest {

    @Mock
    private ChatDataRepository chatDataRepository;

    @InjectMocks
    private DeleteChatService service;

    @Test
    void shouldDeleteChat() {
        when(chatDataRepository.removeChat(new Chat(1L))).thenReturn(true);

        service.deleteChat(1L);

        verify(chatDataRepository).removeChat(new Chat(1L));
    }

    @Test
    void shouldFailWhenChatDoesNotExist() {
        when(chatDataRepository.removeChat(new Chat(1L))).thenReturn(false);

        assertThatThrownBy(() -> service.deleteChat(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chat not found");
    }
}
