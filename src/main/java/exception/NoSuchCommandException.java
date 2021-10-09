package exception;

public class NoSuchCommandException extends RuntimeException {

    public NoSuchCommandException() {
    }

    /**
     * Creates a new instance.
     */
    public NoSuchCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public NoSuchCommandException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public NoSuchCommandException(Throwable cause) {
        super(cause);
    }
}
