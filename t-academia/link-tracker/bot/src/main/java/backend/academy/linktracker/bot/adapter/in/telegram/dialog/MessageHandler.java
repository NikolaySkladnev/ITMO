package backend.academy.linktracker.bot.adapter.in.telegram.dialog;

import backend.academy.linktracker.bot.domain.entities.TrackingDialogState;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;

@Getter
public class MessageHandler {

    private final List<Predicate<MessageHandlerContext>> filters;
    private final Function<MessageHandlerContext, SendMessage> method;
    private final TrackingDialogState nextState;
    private final String invalidInputMessage;
    private final String alreadyTrackedMessage;

    private MessageHandler(
            List<Predicate<MessageHandlerContext>> filters,
            Function<MessageHandlerContext, SendMessage> method,
            TrackingDialogState nextState,
            String invalidInputMessage,
            String alreadyTrackedMessage) {
        this.filters = List.copyOf(filters);
        this.method = method;
        this.nextState = nextState;
        this.invalidInputMessage = invalidInputMessage;
        this.alreadyTrackedMessage = alreadyTrackedMessage;
    }

    public boolean matches(MessageHandlerContext context) {
        return filters.stream().allMatch(filter -> filter.test(context));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<Predicate<MessageHandlerContext>> filters = new ArrayList<>();
        private Function<MessageHandlerContext, SendMessage> method;
        private TrackingDialogState nextState;
        private String invalidInputMessage;
        private String alreadyTrackedMessage;

        public Builder withFilter(Predicate<MessageHandlerContext> filter) {
            this.filters.add(filter);
            return this;
        }

        public Builder method(Function<MessageHandlerContext, SendMessage> method) {
            this.method = method;
            return this;
        }

        public Builder nextState(TrackingDialogState nextState) {
            this.nextState = nextState;
            return this;
        }

        public Builder invalidInputMessage(String invalidInputMessage) {
            this.invalidInputMessage = invalidInputMessage;
            return this;
        }

        public Builder alreadyTrackedMessage(String alreadyTrackedMessage) {
            this.alreadyTrackedMessage = alreadyTrackedMessage;
            return this;
        }

        public MessageHandler build() {
            return new MessageHandler(filters, method, nextState, invalidInputMessage, alreadyTrackedMessage);
        }
    }
}
