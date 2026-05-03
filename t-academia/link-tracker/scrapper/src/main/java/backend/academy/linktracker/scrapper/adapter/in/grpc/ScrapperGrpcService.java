package backend.academy.linktracker.scrapper.adapter.in.grpc;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;
import backend.academy.linktracker.scrapper.application.port.in.AddLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.in.DeleteChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.GetAllLinksUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RegisterChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RemoveLinkUseCase;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import proto.Scrapper;
import proto.ScrapperServiceGrpc;

@Component
@RequiredArgsConstructor
public class ScrapperGrpcService extends ScrapperServiceGrpc.ScrapperServiceImplBase {

    private final RegisterChatUseCase registerChatUseCase;
    private final DeleteChatUseCase deleteChatUseCase;
    private final GetAllLinksUseCase getAllLinksUseCase;
    private final AddLinkUseCase addLinkUseCase;
    private final RemoveLinkUseCase removeLinkUseCase;

    @Override
    public void registerChat(Scrapper.RegisterChatRequest request, StreamObserver<Empty> responseObserver) {
        try {
            registerChatUseCase.registerChat(request.getId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void deleteChat(Scrapper.DeleteChatRequest request, StreamObserver<Empty> responseObserver) {
        try {
            deleteChatUseCase.deleteChat(request.getId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void getAllTrackedLinks(
            Scrapper.GetAllTrackedLinksRequest request, StreamObserver<Scrapper.ListLinksResponse> responseObserver) {
        try {
            ListLinkResult response = getAllLinksUseCase.getAllLinks(request.getTgChatId());

            responseObserver.onNext(Scrapper.ListLinksResponse.newBuilder()
                    .addAllLinks(response.links().stream()
                            .map(l -> Scrapper.LinkResponse.newBuilder()
                                    .setId(l.id())
                                    .setUrl(l.url())
                                    .addAllTags(l.tags())
                                    .addAllFilters(l.filters())
                                    .build())
                            .toList())
                    .setSize(response.size())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void addLink(Scrapper.AddLinkRequest request, StreamObserver<Scrapper.LinkResponse> responseObserver) {
        try {
            LinkResult response = addLinkUseCase.addLink(
                    request.getTgChatId(), request.getLink(), request.getTagsList(), request.getFiltersList());

            responseObserver.onNext(Scrapper.LinkResponse.newBuilder()
                    .setId(response.id())
                    .setUrl(response.url())
                    .addAllTags(response.tags())
                    .addAllFilters(response.filters())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void removeLink(Scrapper.RemoveLinkRequest request, StreamObserver<Scrapper.LinkResponse> responseObserver) {
        try {
            LinkResult response = removeLinkUseCase.removeLink(request.getTgChatId(), request.getLink());

            responseObserver.onNext(Scrapper.LinkResponse.newBuilder()
                    .setId(response.id())
                    .setUrl(response.url())
                    .addAllTags(response.tags())
                    .addAllFilters(response.filters())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    private RuntimeException mapException(Exception e) {
        String message = e.getMessage() == null ? "" : e.getMessage();

        if (e instanceof IllegalStateException && message.toLowerCase().contains("already")) {
            return Status.ALREADY_EXISTS.withDescription(message).asRuntimeException();
        }

        if (e instanceof IllegalArgumentException && message.toLowerCase().contains("not found")) {
            return Status.NOT_FOUND.withDescription(message).asRuntimeException();
        }

        if (e instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException();
        }

        return Status.INTERNAL.withDescription("Internal server error").asRuntimeException();
    }
}
