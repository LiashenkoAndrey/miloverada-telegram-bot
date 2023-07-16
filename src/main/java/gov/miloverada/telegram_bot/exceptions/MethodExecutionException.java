package gov.miloverada.telegram_bot.exceptions;

public class MethodExecutionException extends UpdateProcessingException {

    public MethodExecutionException(Throwable cause) {
        super(cause);
    }

    public MethodExecutionException(String message) {
        super(message);
    }
}
