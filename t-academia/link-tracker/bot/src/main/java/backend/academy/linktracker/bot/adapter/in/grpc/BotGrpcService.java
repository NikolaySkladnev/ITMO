package backend.academy.linktracker.bot.adapter.in.grpc;

import backend.academy.linktracker.bot.application.port.in.ProcessBotUpdateUseCase;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import proto.Bot;
import proto.BotServiceGrpc;

@Component
@RequiredArgsConstructor
public class BotGrpcService extends BotServiceGrpc.BotServiceImplBase {

    private final ProcessBotUpdateUseCase botUpdateUseCase;

    @Override
    public void sendUpdates(Bot.LinkUpdate request, StreamObserver<Empty> responseObserver) {
        try {
            botUpdateUseCase.process(
                    request.getId(), request.getUrl(), request.getDescription(), request.getTgChatIdsList());

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }
}
