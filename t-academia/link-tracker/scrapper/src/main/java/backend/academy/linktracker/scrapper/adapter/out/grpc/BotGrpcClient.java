package backend.academy.linktracker.scrapper.adapter.out.grpc;

import backend.academy.linktracker.scrapper.application.dto.UpdateNotification;
import backend.academy.linktracker.scrapper.application.port.out.BotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import proto.Bot;
import proto.BotServiceGrpc;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.bot", name = "transport", havingValue = "grpc")
public class BotGrpcClient implements BotClient {
    private final BotServiceGrpc.BotServiceBlockingStub stub;

    @Override
    public void sendUpdate(UpdateNotification update) {
        Bot.LinkUpdate request = Bot.LinkUpdate.newBuilder()
                .setId(update.id())
                .setUrl(update.url())
                .setDescription(update.description())
                .addAllTgChatIds(update.chatIds())
                .build();

        stub.sendUpdates(request);
    }
}
