package backend.academy.linktracker.bot.infrastructure.properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.bot.grpc")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BotGrpcProperties {

    private int serverPort;
}
