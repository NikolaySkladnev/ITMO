package backend.academy.linktracker.scrapper.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.grpc")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ScrapperGrpcProperties {

    private int serverPort;

    @NotBlank
    private String botHost;

    private int botPort;
}
