package backend.academy.linktracker.scrapper.adapter.in.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;
import backend.academy.linktracker.scrapper.application.port.in.AddLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.in.DeleteChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.GetAllLinksUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RegisterChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RemoveLinkUseCase;
import backend.academy.linktracker.scrapper.support.TestStreamObserver;
import com.google.protobuf.Empty;
import io.grpc.Status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import proto.Scrapper;

@ExtendWith(MockitoExtension.class)
class ScrapperGrpcServiceTest {

    @Mock
    private RegisterChatUseCase registerChatUseCase;

    @Mock
    private DeleteChatUseCase deleteChatUseCase;

    @Mock
    private GetAllLinksUseCase getAllLinksUseCase;

    @Mock
    private AddLinkUseCase addLinkUseCase;

    @Mock
    private RemoveLinkUseCase removeLinkUseCase;

    @InjectMocks
    private ScrapperGrpcService service;

    @Test
    void shouldRegisterChat() {
        TestStreamObserver<Empty> observer = new TestStreamObserver<>();

        service.registerChat(Scrapper.RegisterChatRequest.newBuilder().setId(1L).build(), observer);

        assertThat(observer.values()).containsExactly(Empty.getDefaultInstance());
        assertThat(observer.completed()).isTrue();
        assertThat(observer.error()).isNull();
    }

    @Test
    void shouldMapAlreadyExistsToGrpcStatus() {
        TestStreamObserver<Empty> observer = new TestStreamObserver<>();
        Mockito.doThrow(new IllegalStateException("Chat already exists: 1"))
                .when(registerChatUseCase)
                .registerChat(1L);

        service.registerChat(Scrapper.RegisterChatRequest.newBuilder().setId(1L).build(), observer);

        assertThat(Status.fromThrowable(observer.error()).getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
    }

    @Test
    void shouldMapDeleteNotFoundToGrpcStatus() {
        TestStreamObserver<Empty> observer = new TestStreamObserver<>();
        Mockito.doThrow(new IllegalArgumentException("Chat not found: 1"))
                .when(deleteChatUseCase)
                .deleteChat(1L);

        service.deleteChat(Scrapper.DeleteChatRequest.newBuilder().setId(1L).build(), observer);

        assertThat(Status.fromThrowable(observer.error()).getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    void shouldReturnAllTrackedLinks() {
        TestStreamObserver<Scrapper.ListLinksResponse> observer = new TestStreamObserver<>();
        when(getAllLinksUseCase.getAllLinks(1L))
                .thenReturn(new ListLinkResult(
                        List.of(new LinkResult(
                                1L, "https://github.com/openai/openai-java", List.of("work"), List.of())),
                        1));

        service.getAllTrackedLinks(
                Scrapper.GetAllTrackedLinksRequest.newBuilder().setTgChatId(1L).build(), observer);

        assertThat(observer.values()).hasSize(1);
        assertThat(observer.values().get(0).getSize()).isEqualTo(1);
        assertThat(observer.values().get(0).getLinks(0).getUrl()).isEqualTo("https://github.com/openai/openai-java");
    }

    @Test
    void shouldAddLink() {
        TestStreamObserver<Scrapper.LinkResponse> observer = new TestStreamObserver<>();
        when(addLinkUseCase.addLink(1L, "https://github.com/openai/openai-java", List.of("work"), List.of()))
                .thenReturn(new LinkResult(1L, "https://github.com/openai/openai-java", List.of("work"), List.of()));

        service.addLink(
                Scrapper.AddLinkRequest.newBuilder()
                        .setTgChatId(1L)
                        .setLink("https://github.com/openai/openai-java")
                        .addAllTags(List.of("work"))
                        .build(),
                observer);

        assertThat(observer.values()).hasSize(1);
        assertThat(observer.values().get(0).getUrl()).isEqualTo("https://github.com/openai/openai-java");
    }

    @Test
    void shouldMapRemoveLinkNotFoundToGrpcStatus() {
        TestStreamObserver<Scrapper.LinkResponse> observer = new TestStreamObserver<>();
        Mockito.doThrow(new IllegalArgumentException("Link not found: https://github.com/openai/openai-java"))
                .when(removeLinkUseCase)
                .removeLink(1L, "https://github.com/openai/openai-java");

        service.removeLink(
                Scrapper.RemoveLinkRequest.newBuilder()
                        .setTgChatId(1L)
                        .setLink("https://github.com/openai/openai-java")
                        .build(),
                observer);

        assertThat(Status.fromThrowable(observer.error()).getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }
}
