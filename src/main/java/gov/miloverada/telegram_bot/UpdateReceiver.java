package gov.miloverada.telegram_bot;

import gov.miloverada.telegram_bot.exceptions.MethodExecutionException;
import gov.miloverada.telegram_bot.exceptions.UpdateProcessingException;
import gov.miloverada.telegram_bot.interfaces.MethodExecutor;
import gov.miloverada.telegram_bot.util.UserCash;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UpdateReceiver {

    private static final Logger logger = LogManager.getLogger(UpdateReceiver.class);

    private final MethodExecutor executor;

    private final UpdateDetails updateDetails;

    private final UserCashService cashService;

    public void processUpdate(Update update) throws UpdateProcessingException {
        try {
            if (update.hasCallbackQuery()) logger.debug(update.getCallbackQuery().getData());
            else logger.debug(update.getMessage().getText());

            updateDetails.update(update);

            if (hasCurrentMethod(update)) {
                executor.invokeUtilMethod(getCurrentMethod(update));
            } else {

                if (update.hasCallbackQuery()) {
                    executor.invokeCallBack(update);
                } else if (getMessage(update).getText().charAt(0) == '/'){
                    executor.invokeCommand(update);
                }
            }

        } catch (MethodExecutionException e) {
            logger.error(e.getMessage());
            throw new UpdateProcessingException(e);
        }
    }

    private String getCurrentMethod(Update update) {
        Message message = getMessage(update);
        UserCash cash = cashService.getUserCash(message.getFrom().getId());
        return cash.getCurrentMethod();

    }

    private boolean hasCurrentMethod(Update update) {
        Long id;
        if (update.hasCallbackQuery()) {
            id = update.getCallbackQuery().getFrom().getId();
        } else {
            id = update.getMessage().getFrom().getId();
        }
        UserCash cash = cashService.getUserCash(id);
        return cash.getCurrentMethod() != null;
    }

    private Message getMessage(Update update) {
        Message message;
        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        } else {
            message = update.getMessage();
        }

        return message;
    }


}