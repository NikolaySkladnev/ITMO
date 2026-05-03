package backend.academy.linktracker.scrapper.adapter.in.rest;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;
import backend.academy.linktracker.scrapper.application.port.in.AddLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.in.DeleteChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.GetAllLinksUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RegisterChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RemoveLinkUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScrapperRestController {

    private final RegisterChatUseCase registerChatUseCase;
    private final DeleteChatUseCase deleteChatUseCase;
    private final GetAllLinksUseCase getAllLinksUseCase;
    private final AddLinkUseCase addLinkUseCase;
    private final RemoveLinkUseCase removeLinkUseCase;

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable long id) {
        registerChatUseCase.registerChat(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable long id) {
        deleteChatUseCase.deleteChat(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") long chatId) {
        ListLinkResult result = getAllLinksUseCase.getAllLinks(chatId);

        List<LinkResponse> links = result.links().stream().map(this::toResponse).toList();

        return ResponseEntity.ok(new ListLinksResponse(links, result.size()));
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLink(
            @RequestHeader("Tg-Chat-Id") long chatId, @Valid @RequestBody AddLinkRequest request) {

        LinkResult result = addLinkUseCase.addLink(
                chatId,
                request.link(),
                request.tags() == null ? List.of() : request.tags(),
                request.filters() == null ? List.of() : request.filters());

        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> removeLink(
            @RequestHeader("Tg-Chat-Id") long chatId, @Valid @RequestBody RemoveLinkRequest request) {

        LinkResult result = removeLinkUseCase.removeLink(chatId, request.link());
        return ResponseEntity.ok(toResponse(result));
    }

    private LinkResponse toResponse(LinkResult result) {
        return new LinkResponse(result.id(), result.url(), result.tags(), result.filters());
    }

    public record AddLinkRequest(String link, List<String> tags, List<String> filters) {}

    public record RemoveLinkRequest(@NotBlank String link) {}

    public record LinkResponse(long id, String url, List<String> tags, List<String> filters) {}

    public record ListLinksResponse(List<LinkResponse> links, int size) {}
}
