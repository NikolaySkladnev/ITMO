package backend.academy.linktracker.bot.adapter.in.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.application.port.in.ProcessBotUpdateUseCase;
import backend.academy.linktracker.bot.support.TestStreamObserver;
import com.google.protobuf.Empty;
import io.grpc.Status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import proto.Bot;

@ExtendWith(MockitoExtension.class)
class BotGrpcServiceTest {

    @Mock
    private ProcessBotUpdateUseCase botUpdateUseCase;

    @InjectMocks
    private BotGrpcService service;

    @Test
    void shouldProcessUpdateAndComplete() {
        TestStreamObserver<Empty> observer = new TestStreamObserver<>();
        Bot.LinkUpdate request = Bot.LinkUpdate.newBuilder()
                .setId(1L)
                .setUrl("https://github.com/openai/openai-java")
                .setDescription("updated")
                .addAllTgChatIds(List.of(1L, 2L))
                .build();

        service.sendUpdates(request, observer);

        verify(botUpdateUseCase).process(1L, "https://github.com/openai/openai-java", "updated", List.of(1L, 2L));
        assertThat(observer.values()).containsExactly(Empty.getDefaultInstance());
        assertThat(observer.completed()).isTrue();
        assertThat(observer.error()).isNull();
    }

    @Test
    void shouldReturnInvalidArgumentOnValidationError() {
        TestStreamObserver<Empty> observer = new TestStreamObserver<>();
        Bot.LinkUpdate request = Bot.LinkUpdate.newBuilder().setId(1L).build();
        Mockito.doThrow(new IllegalArgumentException("bad request"))
                .when(botUpdateUseCase)
                .process(1L, "", "", List.of());

        service.sendUpdates(request, observer);

        assertThat(Status.fromThrowable(observer.error()).getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
    }

    @Test
    void shouldReturnInternalOnUnexpectedFailure() {
        TestStreamObserver<Empty> observer = new TestStreamObserver<>();
        Bot.LinkUpdate request = Bot.LinkUpdate.newBuilder().setId(1L).build();
        Mockito.doThrow(new RuntimeException("boom")).when(botUpdateUseCase).process(1L, "", "", List.of());

        service.sendUpdates(request, observer);

        assertThat(Status.fromThrowable(observer.error()).getCode()).isEqualTo(Status.Code.INTERNAL);
    }
}
