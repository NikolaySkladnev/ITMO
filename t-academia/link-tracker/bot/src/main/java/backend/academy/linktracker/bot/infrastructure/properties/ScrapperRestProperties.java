package backend.academy.linktracker.bot.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.scrapper.rest")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ScrapperRestProperties {

    @NotBlank
    private String baseUrl = "http://localhost:8081";
}
