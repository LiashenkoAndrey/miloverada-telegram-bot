package gov.miloverada.telegram_bot.controllers.exceptions;

import gov.miloverada.telegram_bot.exceptions.UpdateProcessingException;

public class MethodExecutionException extends UpdateProcessingException {

    public MethodExecutionException(Throwable cause) {
        super(cause);
    }

    public MethodExecutionException(String message) {
        super(message);
    }
}
