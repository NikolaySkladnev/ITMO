package backend.academy.linktracker.bot.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.linktracker.bot.adapter.in.rest.BotRestController;
import backend.academy.linktracker.bot.application.port.in.ProcessBotUpdateUseCase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("contract")
@WebMvcTest(BotRestController.class)
class BotHttpContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessBotUpdateUseCase botUpdateUseCase;

    @Test
    void shouldAcceptValidPostUpdatesRequest() throws Exception {
        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "id": 1,
                          "url": "https://github.com/openai/openai-java",
                          "description": "Repo updated",
                          "tgChatIds": [1, 2]
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidPostUpdatesRequest() throws Exception {
        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "id": "wrong-type",
                          "url": 42,
                          "tgChatIds": "not-an-array"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
