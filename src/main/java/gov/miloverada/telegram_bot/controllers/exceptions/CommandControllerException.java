package gov.miloverada.telegram_bot.controllers.exceptions;

public class CommandControllerException extends ControllerException {

    public CommandControllerException(String message) {
        super(message);
    }

    public CommandControllerException(Throwable cause) {
        super(cause);
    }
}
