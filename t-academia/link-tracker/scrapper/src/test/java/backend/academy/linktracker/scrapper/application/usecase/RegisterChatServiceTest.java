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
class RegisterChatServiceTest {

    @Mock
    private ChatDataRepository chatDataRepository;

    @InjectMocks
    private RegisterChatService service;

    @Test
    void shouldRegisterChat() {
        when(chatDataRepository.hasChat(new Chat(1L))).thenReturn(false);
        when(chatDataRepository.addChat(new Chat(1L))).thenReturn(true);

        service.registerChat(1L);

        verify(chatDataRepository).addChat(new Chat(1L));
    }

    @Test
    void shouldFailWhenChatAlreadyExists() {
        when(chatDataRepository.hasChat(new Chat(1L))).thenReturn(true);

        assertThatThrownBy(() -> service.registerChat(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Chat already exists");
    }
}
