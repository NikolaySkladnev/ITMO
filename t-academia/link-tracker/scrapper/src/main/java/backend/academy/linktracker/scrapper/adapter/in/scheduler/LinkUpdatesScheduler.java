package backend.academy.linktracker.scrapper.adapter.in.scheduler;

import backend.academy.linktracker.scrapper.application.port.in.CheckUpdatesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LinkUpdatesScheduler {

    private final CheckUpdatesUseCase checkUpdatesUseCase;

    @Scheduled(fixedDelayString = "${app.scheduler.interval:60000}")
    public void checkUpdates() {
        log.info("Starting scheduled link updates check");
        checkUpdatesUseCase.checkUpdates();
    }
}
