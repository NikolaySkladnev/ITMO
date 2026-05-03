package backend.academy.linktracker.scrapper.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAllLinksServiceTest {

    @Mock
    private ChatDataRepository chatDataRepository;

    @InjectMocks
    private GetAllLinksService service;

    @Test
    void shouldReturnAllLinksForChat() {
        long chatId = 1L;
        when(chatDataRepository.hasChat(new Chat(chatId))).thenReturn(true);
        when(chatDataRepository.getAllLinksByChat(new Chat(chatId)))
                .thenReturn(List.of(
                        new Link("https://github.com/openai/openai-java", List.of("work")),
                        new Link("https://stackoverflow.com/questions/1/example", List.of("docs"))));

        ListLinkResult result = service.getAllLinks(chatId);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.links())
                .extracting(LinkResult::url)
                .containsExactly(
                        "https://github.com/openai/openai-java", "https://stackoverflow.com/questions/1/example");
    }

    @Test
    void shouldFailWhenChatDoesNotExist() {
        when(chatDataRepository.hasChat(new Chat(1L))).thenReturn(false);

        assertThatThrownBy(() -> service.getAllLinks(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chat not found");
    }
}
