package gov.miloverada.telegram_bot.controllers.exceptions;

public class CallBackControllerException extends ControllerException {

    public CallBackControllerException(String message) {
        super(message);
    }

    public CallBackControllerException(Throwable cause) {
        super(cause);
    }
}
