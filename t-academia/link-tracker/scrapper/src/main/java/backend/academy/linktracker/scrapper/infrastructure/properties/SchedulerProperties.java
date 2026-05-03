package backend.academy.linktracker.scrapper.infrastructure.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.scheduler")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class SchedulerProperties {

    @Min(1)
    private long interval = 60000L;

    @Min(50)
    @Max(500)
    private int batchSize = 100;

    @Min(1)
    private int parallelism = 4;
}
