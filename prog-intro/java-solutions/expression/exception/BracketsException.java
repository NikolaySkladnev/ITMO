package expression.exception;

public class BracketsException extends RuntimeException{
    public BracketsException() {
    }

    public BracketsException(String message) {
        super(message);
    }

    public BracketsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BracketsException(Throwable cause) {
        super(cause);
    }

    public BracketsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
