package backend.academy.linktracker.scrapper.application.usecase;

import backend.academy.linktracker.scrapper.application.dto.LinkProcessingReport;
import backend.academy.linktracker.scrapper.application.port.in.CheckUpdatesUseCase;
import backend.academy.linktracker.scrapper.application.service.TrackedLinkBatchProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckUpdatesService implements CheckUpdatesUseCase {

    private final TrackedLinkBatchProcessor trackedLinkBatchProcessor;

    @Override
    public void checkUpdates() {
        LinkProcessingReport report = trackedLinkBatchProcessor.processAll();

        if (report.failedLinks().isEmpty()) {
            log.info(
                    "Finished link updates check: checkedLinks={}, foundUpdates={}",
                    report.checkedLinks(),
                    report.foundUpdates());
            return;
        }

        log.warn(
                "Finished link updates check: checkedLinks={}, foundUpdates={}, failedLinks={}",
                report.checkedLinks(),
                report.foundUpdates(),
                report.failedLinks());
    }
}
