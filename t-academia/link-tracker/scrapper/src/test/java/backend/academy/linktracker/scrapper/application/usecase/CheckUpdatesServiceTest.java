package backend.academy.linktracker.scrapper.application.usecase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.scrapper.application.dto.LinkProcessingReport;
import backend.academy.linktracker.scrapper.application.service.TrackedLinkBatchProcessor;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckUpdatesServiceTest {

    @Mock
    private TrackedLinkBatchProcessor trackedLinkBatchProcessor;

    @InjectMocks
    private CheckUpdatesService service;

    @Test
    void shouldDelegateToTrackedLinkBatchProcessor() {
        when(trackedLinkBatchProcessor.processAll()).thenReturn(new LinkProcessingReport(0, 0, List.of()));

        service.checkUpdates();

        verify(trackedLinkBatchProcessor).processAll();
    }
}
