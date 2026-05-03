package backend.academy.linktracker.bot.application.usecase;

import backend.academy.linktracker.bot.application.port.in.StartTrackDialogUseCase;
import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StartTrackDialogService implements StartTrackDialogUseCase {

    private final UserStateRepository userStateRepository;
    private final PendingLinkRepository pendingLinkRepository;

    @Override
    public void start(long chatId) {
        pendingLinkRepository.clear(chatId);
        userStateRepository.setState(chatId, TrackingDialogState.WAIT_LINK);
    }
}
