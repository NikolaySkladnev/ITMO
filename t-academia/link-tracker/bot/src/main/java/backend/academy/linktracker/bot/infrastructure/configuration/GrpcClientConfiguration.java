package backend.academy.linktracker.bot.infrastructure.configuration;

import backend.academy.linktracker.bot.infrastructure.properties.ScrapperGrpcProperties;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proto.ScrapperServiceGrpc;

@Configuration
public class GrpcClientConfiguration {

    @Bean
    public ManagedChannel scrapperManagedChannel(ScrapperGrpcProperties properties) {
        return ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                .usePlaintext()
                .build();
    }

    @Bean
    public ScrapperServiceGrpc.ScrapperServiceBlockingStub scrapperBlockingStub(ManagedChannel scrapperManagedChannel) {
        return ScrapperServiceGrpc.newBlockingStub(scrapperManagedChannel);
    }
}
