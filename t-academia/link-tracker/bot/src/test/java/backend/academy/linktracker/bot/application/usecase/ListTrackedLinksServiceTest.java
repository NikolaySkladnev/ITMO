package backend.academy.linktracker.bot.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListTrackedLinksServiceTest {

    @Mock
    private ScrapperClient scrapperClient;

    @InjectMocks
    private ListTrackedLinksService service;

    @Test
    void shouldReturnAllLinksWhenTagIsMissing() {
        List<TrackedLinkDto> links = List.of(
                new TrackedLinkDto(1L, "https://github.com/openai/openai-java", List.of("work")),
                new TrackedLinkDto(2L, "https://stackoverflow.com/questions/1/example", List.of("docs")));
        when(scrapperClient.getLinks(10L)).thenReturn(links);

        assertThat(service.getLinks(10L, null)).containsExactlyElementsOf(links);
    }

    @Test
    void shouldReturnAllLinksWhenTagIsBlank() {
        List<TrackedLinkDto> links =
                List.of(new TrackedLinkDto(1L, "https://github.com/openai/openai-java", List.of("work")));
        when(scrapperClient.getLinks(10L)).thenReturn(links);

        assertThat(service.getLinks(10L, "   ")).containsExactlyElementsOf(links);
    }

    @Test
    void shouldFilterLinksByTag() {
        when(scrapperClient.getLinks(10L))
                .thenReturn(List.of(
                        new TrackedLinkDto(1L, "https://github.com/openai/openai-java", List.of("work", "java")),
                        new TrackedLinkDto(2L, "https://stackoverflow.com/questions/1/example", List.of("docs"))));

        assertThat(service.getLinks(10L, "work"))
                .extracting(TrackedLinkDto::url)
                .containsExactly("https://github.com/openai/openai-java");
    }
}
