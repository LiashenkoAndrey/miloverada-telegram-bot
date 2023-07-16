package gov.miloverada.telegram_bot.exceptions;

public class CommandMethodExecutionException extends MethodExecutionException {

    public CommandMethodExecutionException(Throwable cause) {
        super(cause);
    }

    public CommandMethodExecutionException(String message) {
        super(message);
    }
}
