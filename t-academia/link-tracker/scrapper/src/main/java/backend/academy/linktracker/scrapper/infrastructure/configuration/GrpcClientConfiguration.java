package backend.academy.linktracker.scrapper.infrastructure.configuration;

import backend.academy.linktracker.scrapper.infrastructure.properties.ScrapperGrpcProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proto.BotServiceGrpc;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    public ManagedChannel botManagedChannel(ScrapperGrpcProperties properties) {
        return ManagedChannelBuilder.forAddress(properties.getBotHost(), properties.getBotPort())
                .usePlaintext()
                .build();
    }

    @Bean
    public BotServiceGrpc.BotServiceBlockingStub botBlockingStub(ManagedChannel botManagedChannel) {
        return BotServiceGrpc.newBlockingStub(botManagedChannel);
    }
}
