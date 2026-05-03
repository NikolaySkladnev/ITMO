package backend.academy.linktracker.bot.adapter.out.grpc;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import backend.academy.linktracker.bot.application.port.out.ScrapperClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import proto.Scrapper;
import proto.ScrapperServiceGrpc;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.scrapper", name = "transport", havingValue = "grpc", matchIfMissing = true)
public class ScrapperGrpcClient implements ScrapperClient {

    private final ScrapperServiceGrpc.ScrapperServiceBlockingStub stub;

    @Override
    public void registerChat(long chatId) {
        Scrapper.RegisterChatRequest request =
                Scrapper.RegisterChatRequest.newBuilder().setId(chatId).build();

        try {
            stub.registerChat(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                return;
            }
            throw e;
        }
    }

    @Override
    public void deleteChat(long chatId) {
        Scrapper.DeleteChatRequest request =
                Scrapper.DeleteChatRequest.newBuilder().setId(chatId).build();

        stub.deleteChat(request);
    }

    @Override
    public void addLink(long chatId, String url, List<String> tags) {
        Scrapper.AddLinkRequest request = Scrapper.AddLinkRequest.newBuilder()
                .setTgChatId(chatId)
                .setLink(url)
                .addAllTags(tags)
                .addAllFilters(List.of())
                .build();

        stub.addLink(request);
    }

    @Override
    public void removeLink(long chatId, String url) {
        Scrapper.RemoveLinkRequest request = Scrapper.RemoveLinkRequest.newBuilder()
                .setTgChatId(chatId)
                .setLink(url)
                .build();

        stub.removeLink(request);
    }

    @Override
    public List<TrackedLinkDto> getLinks(long chatId) {
        Scrapper.GetAllTrackedLinksRequest request = Scrapper.GetAllTrackedLinksRequest.newBuilder()
                .setTgChatId(chatId)
                .build();

        return stub.getAllTrackedLinks(request).getLinksList().stream()
                .map(link -> new TrackedLinkDto(link.getId(), link.getUrl(), link.getTagsList()))
                .toList();
    }
}
