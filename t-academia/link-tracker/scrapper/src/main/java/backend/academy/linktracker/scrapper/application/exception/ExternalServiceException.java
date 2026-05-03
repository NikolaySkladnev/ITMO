package backend.academy.linktracker.scrapper.application.exception;

import java.io.Serial;

public class ExternalServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
