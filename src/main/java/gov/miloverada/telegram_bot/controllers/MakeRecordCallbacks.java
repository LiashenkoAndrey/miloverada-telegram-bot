package gov.miloverada.telegram_bot.controllers;

import gov.miloverada.telegram_bot.UpdateReceiver;
import gov.miloverada.telegram_bot.controllers.exceptions.CallBackControllerException;
import gov.miloverada.telegram_bot.util.annotations.CallBack;
import gov.miloverada.telegram_bot.util.annotations.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Controller
@Component
public class MakeRecordCallbacks extends AbsController {

    private static final Logger logger = LogManager.getLogger(UpdateReceiver.class);

    @CallBack
    public void makeRecord() {
        getName();
    }

    public void getName() {
        try {
            if (cash().hasCurrentMethod()) {
                String fullNameWithSurname = message().getText();

                String[] arr = fullNameWithSurname.split(" ");

                if (arr.length != 3) {
                    Message msg = bot.execute(SendMessage.builder()
                            .chatId(chatId())
                            .text("ПІБ некоректне, спробуйте ще.\nДотримуйтесь формату:\nФранчук Аліна Миколаївна")

                            .build());

                    cash().addLastUserMessage(msg);
                } else {
                    deleteLastUserMessages();
                    cash().getUser().setFullNameWithSurname(fullNameWithSurname);
                    cash().setCurrentMethod(null);
                    getPhoneNumber();
                }

            } else {

                bot.execute(EditMessageText.builder()
                        .chatId(chatId())
                        .messageId(cash().getLastMessageId())
                        .text("Введіть ваше ПІБ (прізвище ім'я по батькові)")
                        .build());

                cash().setCurrentMethod("getName");
            }
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new CallBackControllerException(e);
        }
    }

    public void getPhoneNumber() {
        try {
            if (cash().hasCurrentMethod()) {
                String phoneNumber;

                Contact contact = message().getContact();
                if (contact != null) {
                        phoneNumber = contact.getPhoneNumber();
                } else {
                    phoneNumber = message().getText();
                }


                if (phoneNumber.length() < 10) {
                    bot.execute(SendMessage.builder()
                            .chatId(chatId())
                            .text("Номер телефону некоректний, спробуйте ще.\nНомер має містити 10 цифр -> ХХХХХХХХХХ")
                            .build());
                } else {
                    deleteLastUserMessages();
                    cash().getUser().setPhoneNumber(phoneNumber);
                    showData();
                }

            } else {

                bot.execute(SendMessage.builder()
                        .chatId(chatId())
                        .text("Введіть ваш номер телефону")
                        .replyMarkup(new ReplyKeyboardMarkup(List.of(
                                new KeyboardRow(List.of(
                                        KeyboardButton.builder()
                                                .requestContact(true)
                                                .text("Надати свій номер телефону")
                                                .build()
                                ))
                        )))
                        .build());

                cash().setCurrentMethod("getPhoneNumber");
            }
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new CallBackControllerException(e);
        }
    }

    public void showData() {
        System.out.println( cash().getUser());
    }

    public void showAllRecordsListByDate(String date) {

    }
}
