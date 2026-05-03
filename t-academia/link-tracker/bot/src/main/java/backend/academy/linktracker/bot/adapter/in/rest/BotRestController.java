package backend.academy.linktracker.bot.adapter.in.rest;

import backend.academy.linktracker.bot.application.port.in.ProcessBotUpdateUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BotRestController {

    private final ProcessBotUpdateUseCase botUpdateUseCase;

    @PostMapping("/updates")
    public ResponseEntity<Void> sendUpdates(@Valid @RequestBody LinkUpdateRequest request) {
        botUpdateUseCase.process(request.id(), request.url(), request.description(), request.tgChatIds());

        return ResponseEntity.ok().build();
    }

    public record LinkUpdateRequest(
            @NotNull Long id,
            @NotBlank String url,
            String description,
            @NotNull @NotEmpty List<@NotNull Long> tgChatIds) {}
}
