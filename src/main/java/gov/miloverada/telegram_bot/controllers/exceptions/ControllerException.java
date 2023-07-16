package gov.miloverada.telegram_bot.controllers.exceptions;

public class ControllerException extends RuntimeException {

    public ControllerException(String message) {
        super(message);
    }

    public ControllerException(Throwable cause) {
        super(cause);
    }
}
