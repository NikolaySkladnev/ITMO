package backend.academy.linktracker.bot.infrastructure.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    private boolean enabled = true;

    @Valid
    private Topics topics = new Topics();

    @Valid
    private Consumer consumer = new Consumer();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Topics {
        @NotBlank
        private String updates = "link-updates";

        @NotBlank
        private String dlq = "link-updates-dlq";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Consumer {
        @NotBlank
        private String groupId = "link-tracker-bot";

        @Min(1)
        private int maxAttempts = 3;
    }
}
