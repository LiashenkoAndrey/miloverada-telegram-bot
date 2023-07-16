package gov.miloverada.telegram_bot.exceptions;

public class UpdateProcessingException extends RuntimeException {

    public UpdateProcessingException(Throwable cause) {
        super(cause);
    }

    public UpdateProcessingException(String message) {
        super(message);
    }
}
