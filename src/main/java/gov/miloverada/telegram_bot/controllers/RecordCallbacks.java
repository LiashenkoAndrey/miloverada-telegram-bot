package gov.miloverada.telegram_bot.controllers;

import gov.miloverada.telegram_bot.controllers.exceptions.CallBackControllerException;
import gov.miloverada.telegram_bot.domain.Record;
import gov.miloverada.telegram_bot.exceptions.RepositoryException;
import gov.miloverada.telegram_bot.interfaces.RecordRepository;
import gov.miloverada.telegram_bot.util.annotations.CallBack;
import gov.miloverada.telegram_bot.util.annotations.Command;
import gov.miloverada.telegram_bot.util.annotations.Controller;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static gov.miloverada.telegram_bot.controllers.ControllerUtils.createRowBtn;

@Controller
@Component
@RequiredArgsConstructor
public class RecordCallbacks extends AbsController {


    private final RecordRepository recordRepository;

    private static final Logger logger = LogManager.getLogger(RecordCallbacks.class);

    private static final String[] daysOfWeek =  {"понеділок", "вівторок", "середа", "четвер", "п'ятниця", "субота", "неділя"};

    @CallBack
    public void saveRecord(String date, String time) {

        try {
            Record record = cash().getRecord();
            record.setDateOfVisit(date);
            record.setTimeOfVisit(time);
            record.setTelegramId(message().getFrom().getId().toString());
            String recordId = recordRepository.saveRecord(record);

            String text;
            String dayOfRecord = daysOfWeek[LocalDate.parse(date).getDayOfWeek().getValue()-1];
            if (dayOfRecord.equals(daysOfWeek[LocalDate.now().getDayOfWeek().getValue()-1])) {
                text = dayOfRecord + ", сьогодні";
            } else {
                text = dayOfRecord;
            }

            bot.execute(EditMessageText.builder()
                            .chatId(chatId())
                            .messageId(cash().getLastBotMessageId())
                            .text("Запис успішний ✅"                +
                                  "\n<b>Дата:</b> "     + date + "  (" +  text + ")" +
                                  "\n<b>Час:</b> "                + time +
                                  "\n<b>Id</b>: "                   + recordId +
                                  "\n\nПерегляд записів - /myRecords" +
                                  "\nМеню - /menu")
                            .parseMode(ParseMode.HTML)
                    .build());
        } catch (TelegramApiException | RepositoryException e) {

            try {
                bot.execute(SendMessage.builder()
                                .chatId(chatId())
                                .text("Виникла неочувана помилка :(\nБудь-ласка спробуйте пізніше")
                        .build());
            } catch (TelegramApiException ex) {
                logger.error(ex.getMessage());
                throw new CallBackControllerException(ex);
            }


            logger.error(e.getMessage());
            throw new CallBackControllerException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.now().getDayOfWeek().getValue());
    }

    @CallBack
    public void createRecord(String serviceId) {
        cash().getRecord().setServiceId(serviceId);
        getName();
    }

    public void getName() {
        try {
            if (cash().hasCurrentMethod()) {
                String fullNameWithSurname = message().getText();

                String[] arr = fullNameWithSurname.split(" ");

                if (arr.length != 3) {
                    cash().addLastUserMessage(message());

                    Message msg = bot.execute(SendMessage.builder()
                            .chatId(chatId())
                            .text("ПІБ некоректне, спробуйте ще.\nДотримуйтесь формату:\nФранчук Аліна Миколаївна")

                            .build());

                    cash().addLastBotMessage(msg);
                } else {
                    cash().addLastUserMessage(message());

                    deleteLastBotMessages();
                    deleteLastBotMessage();
                    deleteLastUserMessages();

                    cash().getRecord().setFirstName(arr[1]);
                    cash().getRecord().setLastName(arr[0]);
                    cash().getRecord().setSurname(arr[2]);
                    cash().setCurrentMethod(null);
                    getPhoneNumber();
                }

            } else {

                bot.execute(EditMessageText.builder()
                        .chatId(chatId())
                        .messageId(cash().getLastBotMessageId())
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
                    Message msg = bot.execute(SendMessage.builder()
                            .chatId(chatId())
                            .text("Номер телефону некоректний, спробуйте ще.\nНомер має містити 10 цифр -> ХХХХХХХХХХ")
                            .build());


                    cash().getLastBotMessages().add(msg.getMessageId());
                    cash().getLastUserMessages().add(messageId());

                } else {

                    deleteLastUserMessages();
                    deleteLastBotMessages();
                    deleteLastBotMessage();
                    deleteMessage(message());

                    cash().getRecord().setPhoneNumber(phoneNumber);
                    cash().setCurrentMethod(null);

                    String serviceId = cash().getRecord().getServiceId();
                    showRecords(serviceId, LocalDate.now().toString());
                }

            } else {

                Message msg = bot.execute(SendMessage.builder()
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

                cash().setLastMessage(msg);
                cash().setCurrentMethod("getPhoneNumber");
            }
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new CallBackControllerException(e);
        }
    }

    private static final List<String> timesList =new ArrayList<>( List.of("08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "13:00", "13:30", "14:00", "14:30", "15:00"));

    @Command
    public void showRecordsNow() {
        showRecords("1", LocalDate.now().toString());
    }

    @CallBack
    public void showRecords(String serviceId, String date) {
        try {
            List<String> times = new ArrayList<>(timesList);
            List<LocalTime> reservedRecords = recordRepository.findRecordsByDate(serviceId, date);

            logger.debug(reservedRecords);

            for (LocalTime reservedRecord : reservedRecords) {
                String recordTime = reservedRecord.toString();
                if (times.contains(recordTime)) {
                    times.set(times.indexOf(recordTime), "---");
                }
            }

            List<List<InlineKeyboardButton>> list = new ArrayList<>();

            int rowLength = 3;
            for (int i = 0; i < times.size(); i= i + rowLength) {
                if (i + rowLength < times.size()) {
                    list.add(createTimesButtons(times, i, i + rowLength, date));
                } else {
                    list.add(createTimesButtons(times, i, times.size(), date));
                }
            }

            LocalDate localDate = LocalDate.parse(date);
            list.add(List.of(
                    createRowBtn("<---", "showRecords?" + serviceId + "_" + localDate.minusDays(1)),
                    createRowBtn("--->", "showRecords?" + serviceId + "_" + localDate.plusDays(1))
            ));

            String text = "Оберіть зручний час та дату\n<b>Дата запису:</b> " + date;

            if (cash().getLastBotMessageId() != null) {
                bot.execute(EditMessageText.builder()
                        .chatId(chatId())
                        .messageId(cash().getLastBotMessageId())
                        .text(text)
                        .replyMarkup(new InlineKeyboardMarkup(list))
                        .parseMode(ParseMode.HTML)
                        .build());

            } else {

                Message msg = bot.execute(SendMessage.builder()
                        .chatId(chatId())
                        .text(text)
                        .replyMarkup(new InlineKeyboardMarkup(list))
                        .parseMode(ParseMode.HTML)
                        .build());

                cash().setLastBotMessageId(msg.getMessageId());
            }


        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new CallBackControllerException(e);
        }
    }

    private List<InlineKeyboardButton> createTimesButtons(List<String> times, int startIndex, int size, String date) {
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        for (int j = startIndex; j < size; j++) {
            buttonList.add(
                    createRowBtn(times.get(j), "saveRecord?"+ date + "_" + times.get(j))
            );
        }
        return buttonList;
    }

}
