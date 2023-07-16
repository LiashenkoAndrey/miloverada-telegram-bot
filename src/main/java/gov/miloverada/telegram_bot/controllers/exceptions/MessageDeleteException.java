package gov.miloverada.telegram_bot.controllers.exceptions;

public class MessageDeleteException extends RuntimeException {

    public MessageDeleteException(Throwable cause) {
        super(cause);
    }
}
