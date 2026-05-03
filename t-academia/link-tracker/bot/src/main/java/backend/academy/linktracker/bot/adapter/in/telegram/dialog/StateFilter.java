package backend.academy.linktracker.bot.adapter.in.telegram.dialog;

import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StateFilter implements Predicate<MessageHandlerContext> {

    private final TrackingDialogState expectedState;

    @Override
    public boolean test(MessageHandlerContext context) {
        return context.state() == expectedState;
    }
}
