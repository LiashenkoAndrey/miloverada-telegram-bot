package gov.miloverada.telegram_bot.exceptions;

public class RepositoryException extends RuntimeException {


    public RepositoryException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RepositoryException(String message) {
        super(message);
    }
}