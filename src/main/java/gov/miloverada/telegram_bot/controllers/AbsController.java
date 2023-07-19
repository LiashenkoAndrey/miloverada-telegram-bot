package gov.miloverada.telegram_bot.controllers;

import gov.miloverada.telegram_bot.Bot;
import gov.miloverada.telegram_bot.UpdateDetails;
import gov.miloverada.telegram_bot.controllers.exceptions.MessageDeleteException;
import gov.miloverada.telegram_bot.util.UserCash;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
public abstract class AbsController {

    private static final Logger logger = LogManager.getLogger(AbsController.class);

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
        if (!cash().getLastUserMessages().isEmpty()) {
            deleteMessages(cash().getLastUserMessages());
            cash().getLastUserMessages().clear();
        }
    }

    protected void deleteLastBotMessages() {
        if (!cash().getLastBotMessages().isEmpty()) {
            deleteMessages(cash().getLastBotMessages());
            cash().getLastBotMessages().clear();
        }
    }

    protected void deleteLastBotMessage() {
        if (cash().getLastBotMessageId() != null) {
            deleteMessages(List.of(cash().getLastBotMessageId()));
            cash().setLastBotMessageId(null);
        }
    }

    protected void deleteMessage(Message message) {
        deleteMessages(List.of(message.getMessageId()));
    }

    private void deleteMessages(List<Integer> list) {
        logger.debug("deleteMessages " + list);
        for (Integer id : list) {
            if (id == null) throw new IllegalArgumentException("id is null");

            try {
                bot.execute(DeleteMessage.builder()
                        .chatId(chatId())
                        .messageId(id)
                        .build());

            } catch (TelegramApiException | IllegalArgumentException e) {
                logger.debug("error id = " + id);
                throw new MessageDeleteException(e);
            }

        }


    }
}
