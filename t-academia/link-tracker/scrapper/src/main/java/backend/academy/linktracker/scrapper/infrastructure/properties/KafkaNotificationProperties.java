package backend.academy.linktracker.scrapper.infrastructure.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.kafka")
@Validated
@Getter
@Setter
@NoArgsConstructor
public class KafkaNotificationProperties {
    @Valid
    private Topics topics = new Topics();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Topics {
        @NotBlank
        private String updates = "link-updates";
    }
}
