package gov.miloverada.telegram_bot.controllers.exceptions;

import gov.miloverada.telegram_bot.exceptions.MethodExecutionException;

public class UtilMethodExecutionException extends MethodExecutionException {
    public UtilMethodExecutionException(Throwable cause) {
        super(cause);
    }

    public UtilMethodExecutionException(String message) {
        super(message);
    }
}
