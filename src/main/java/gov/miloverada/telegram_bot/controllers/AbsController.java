package gov.miloverada.telegram_bot.controllers;

import gov.miloverada.telegram_bot.Bot;
import gov.miloverada.telegram_bot.UpdateDetails;
import gov.miloverada.telegram_bot.controllers.exceptions.MessageDeleteException;
import gov.miloverada.telegram_bot.util.UserCash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public abstract class AbsController {

    @Autowired
    protected Bot bot;
    @Autowired
    protected UpdateDetails updateDetails;

    protected Long chatId() {
        return updateDetails.getChatId();
    }

    protected User user() {
        return updateDetails.getUser();
    }

    protected Message message() {
        return updateDetails.getMessage();
    }

    protected Integer messageId() {
      return updateDetails.getMessageId();
    }

    protected UserCash cash() {
        return updateDetails.getCash();
    }

    protected void deleteLastUserMessages() {
        try {
            for (Integer id : cash().getLastUserMessages()) {
                bot.execute(DeleteMessage.builder()
                        .chatId(chatId())
                        .messageId(id)
                        .build());
            }
        } catch (TelegramApiException e) {
            throw new MessageDeleteException(e);
        }
    }
}
