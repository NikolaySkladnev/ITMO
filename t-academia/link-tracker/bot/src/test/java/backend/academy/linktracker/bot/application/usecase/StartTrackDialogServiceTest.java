package backend.academy.linktracker.bot.application.usecase;

import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartTrackDialogServiceTest {

    @Mock
    private UserStateRepository userStateRepository;

    @Mock
    private PendingLinkRepository pendingLinkRepository;

    @InjectMocks
    private StartTrackDialogService service;

    @Test
    void shouldResetPendingLinkAndSetWaitingForLinkState() {
        service.start(42L);

        verify(pendingLinkRepository).clear(42L);
        verify(userStateRepository).setState(42L, TrackingDialogState.WAIT_LINK);
    }
}
