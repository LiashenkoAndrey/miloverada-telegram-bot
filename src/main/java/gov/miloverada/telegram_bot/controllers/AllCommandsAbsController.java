package gov.miloverada.telegram_bot.controllers;

import gov.miloverada.telegram_bot.UpdateReceiver;
import gov.miloverada.telegram_bot.controllers.exceptions.CommandControllerException;
import gov.miloverada.telegram_bot.util.annotations.Command;
import gov.miloverada.telegram_bot.util.annotations.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.util.List;

import static gov.miloverada.telegram_bot.controllers.ControllerUtils.createOneRowBtn;

@Component
@Controller
public class AllCommandsAbsController extends AbsController {

    private static final Logger logger = LogManager.getLogger(UpdateReceiver.class);


    @Command
    public void help() {
        try {
            bot.execute(SendMessage.builder()
                            .chatId(updateDetails.getChatId())
                            .text("Hello!")
                    .build());

        } catch (TelegramApiException e) {
            logger.info(e.getMessage());
            throw new CommandControllerException(e);
        }
    }

    @Command
    public void start() {
        try {
            LocalTime time = LocalTime.now();
            int hour = time.getHour();
            
            String partOfDay;
            if (hour <= 23 && hour >= 6) {
                partOfDay = "ночі";
            } else if (hour >= 6 && hour <= 12) {
                partOfDay = "ранку";
            } else if (hour >= 12 && hour <= 18) {
                partOfDay = "вечора";
            } else if (hour >= 18 && hour <= 23) {
                partOfDay = "дня";
            } else {
                partOfDay ="здоров'я";
            }

            String text = "Доброго " + partOfDay + "! Я бот від <b>Милівської сільської територіальної громади</b>." +
                    "\nМоє завдання допомогти вам: \n" +
                    "   • Записатися в електронну чергу \n" +
                    "   • Переглянути деталі записів \n" +
                    "Оберіть дію ⬇";
            bot.execute(SendMessage.builder()
                    .chatId(updateDetails.getChatId())
                    .text(text)
                    .replyMarkup(new InlineKeyboardMarkup(List.of(
                                    createOneRowBtn("Записатися в чергу", "makeRecord"),
                                    createOneRowBtn("Переглянути деталі запису", "showRecordDetails")
                    )))
                    .parseMode(ParseMode.HTML)
                    .build());

        } catch (TelegramApiException e) {
            logger.info(e.getMessage());
            throw new CommandControllerException(e);
        }
    }
}
