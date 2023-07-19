package gov.miloverada.telegram_bot.interfaces;

import gov.miloverada.telegram_bot.controllers.exceptions.CallBackMethodExecutionException;
import gov.miloverada.telegram_bot.controllers.exceptions.CommandMethodExecutionException;
import gov.miloverada.telegram_bot.controllers.exceptions.MethodExecutionException;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MethodExecutor {

    void invokeCommand(Update update) throws CommandMethodExecutionException;

    void invokeCallBack(Update update) throws CallBackMethodExecutionException;

    void invokeUtilMethod(String methodName) throws MethodExecutionException;
}
