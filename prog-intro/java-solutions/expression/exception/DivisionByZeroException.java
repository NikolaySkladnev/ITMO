package expression.exception;

public class DivisionByZeroException extends RuntimeException{
    public DivisionByZeroException() {
    }

    public DivisionByZeroException(String message) {
        super(message);
    }

    public DivisionByZeroException(String message, Throwable cause) {
        super(message, cause);
    }

    public DivisionByZeroException(Throwable cause) {
        super(cause);
    }

    public DivisionByZeroException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
