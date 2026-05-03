package backend.academy.linktracker.bot.adapter.out.kafka.exception;

public class DlqPublishingException extends RuntimeException {
    public DlqPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
