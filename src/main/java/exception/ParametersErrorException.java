package exception;

public class ParametersErrorException extends RuntimeException {

    public ParametersErrorException() {
    }

    /**
     * Creates a new instance.
     */
    public ParametersErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public ParametersErrorException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public ParametersErrorException(Throwable cause) {
        super(cause);
    }

}
