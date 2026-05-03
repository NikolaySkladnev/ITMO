package backend.academy.linktracker.scrapper.contract;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.linktracker.scrapper.adapter.in.rest.ScrapperRestController;
import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;
import backend.academy.linktracker.scrapper.application.port.in.AddLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.in.DeleteChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.GetAllLinksUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RegisterChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RemoveLinkUseCase;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("contract")
@WebMvcTest(ScrapperRestController.class)
class ScrapperHttpContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterChatUseCase registerChatUseCase;

    @MockitoBean
    private DeleteChatUseCase deleteChatUseCase;

    @MockitoBean
    private GetAllLinksUseCase getAllLinksUseCase;

    @MockitoBean
    private AddLinkUseCase addLinkUseCase;

    @MockitoBean
    private RemoveLinkUseCase removeLinkUseCase;

    @Test
    void shouldRegisterChatByOpenApiContract() throws Exception {
        mockMvc.perform(post("/tg-chat/1")).andExpect(status().isOk());
    }

    @Test
    void shouldDeleteChatByOpenApiContract() throws Exception {
        mockMvc.perform(delete("/tg-chat/1")).andExpect(status().isOk());
    }

    @Test
    void shouldAddLinkByOpenApiContract() throws Exception {
        when(addLinkUseCase.addLink(1L, "https://github.com/openai/openai-java", List.of("work"), List.of()))
                .thenReturn(new LinkResult(1L, "https://github.com/openai/openai-java", List.of("work"), List.of()));

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "link": "https://github.com/openai/openai-java",
                                  "tags": ["work"],
                                  "filters": []
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldListLinksByOpenApiContract() throws Exception {
        when(getAllLinksUseCase.getAllLinks(1L))
                .thenReturn(new ListLinkResult(
                        List.of(new LinkResult(
                                1L, "https://github.com/openai/openai-java", List.of("work"), List.of())),
                        1));

        mockMvc.perform(get("/links").header("Tg-Chat-Id", "1")).andExpect(status().isOk());
    }

    @Test
    void shouldRemoveLinkByOpenApiContract() throws Exception {
        when(removeLinkUseCase.removeLink(1L, "https://github.com/openai/openai-java"))
                .thenReturn(new LinkResult(1L, "https://github.com/openai/openai-java", List.of("work"), List.of()));

        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "link": "https://github.com/openai/openai-java"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
