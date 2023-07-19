package gov.miloverada.telegram_bot.controllers.exceptions;

public class CommandMethodExecutionException extends MethodExecutionException {

    public CommandMethodExecutionException(Throwable cause) {
        super(cause);
    }

    public CommandMethodExecutionException(String message) {
        super(message);
    }
}
