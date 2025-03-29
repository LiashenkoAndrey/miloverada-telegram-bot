package ua.gov.miloverada.telegrambot.controller;

import static ua.gov.miloverada.telegrambot.Constants.MAX_PHOTOS_SIZE;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.APPLICATION_NEW;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.APPLICATION_NEW_DATA;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.EMAIL_SKIP_ADD;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.EMAIL_SKIP_ADD_DATA;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.IMAGES_SKIP_ADD;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.IMAGES_SKIP_ADD_DATA;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.IMAGES_STOP_ADD;
import static ua.gov.miloverada.telegrambot.util.TelegramBotSender.IMAGES_STOP_ADD_DATA;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.gov.miloverada.telegrambot.service.ApplicationService;
import ua.gov.miloverada.telegrambot.util.TelegramBotSender;
import ua.gov.miloverada.telegrambot.model.UserData;
import ua.gov.miloverada.telegrambot.model.UserState;

/**
 * @author Liashenko Andrii
 * @since 3/29/2025
 */

@RestController
@RequestMapping("/webhook")
@Log4j2
public class TelegramWebhookController {

  private final TelegramBotSender botSender;
  private final ApplicationService applicationService;

  @Autowired
  public TelegramWebhookController(TelegramBotSender botSender, ApplicationService applicationService) {
    this.botSender = botSender;
    this.applicationService = applicationService;
  }

  private final ConcurrentHashMap<Long, UserData> userMap = new ConcurrentHashMap<>(); // Thread-safe storage


  @PostMapping
  public ResponseEntity<String> receiveUpdate(@RequestBody Update update) {
    System.out.println(update);

    if (update.hasCallbackQuery()) {
      System.out.println("Has callback");
      CallbackQuery callbackQuery = update.getCallbackQuery();
      Long chatId = callbackQuery.getMessage().getChatId();
      String callbackData = callbackQuery.getData();

      System.out.println("callbackData: " + callbackData);

      UserData user = userMap.get(chatId);
      if (user != null) {
        handleCallbackData(chatId, callbackData, user);
      } else {
        System.out.println("Skip callback");
      }
    }

    if (update.hasMessage()) {
      Long chatId = update.getMessage().getChatId();
      String text = update.getMessage().getText();
      System.out.println(text);

      if (userMap.get(chatId) == null) {
        UserData data = new UserData();
        data.setUsername(update.getMessage().getFrom().getFirstName());
        userMap.put(chatId, data);
      }

      UserData user = userMap.get(chatId);
      System.out.println("update.hasCallbackQuery() " + update.hasCallbackQuery());

      switch (user.getState()) {
        case INITIAL:
          handleInintialState(text, user, chatId);
          break;
        case FULL_NAME_REQUESTED:
          handleFullNameState(chatId, user, text);
          break;
        case PHONE_REQUESTED:
          handlePhoneState(chatId, update);
          break;
        case EMAIL_REQUESTED:
          handleEmailState(chatId, text, user);
          break;
        case APPLICATION_REQUEST:
          handleApplicationState(chatId, text, user);
          break;
        case PHOTO_REQUEST:
          handlePhotoState(chatId, update, user);
          break;
        case COMPLETION:
          handleCompletion(chatId, user);
          botSender.sendMessage(chatId.toString(), "Ви вже завершили процес реєстрації.");
          break;
      }
    }

    return ResponseEntity.ok("Оновлення отримано");
  }

  private void handleInintialState(String text, UserData user, Long chatId) {
    if (text != null && text.equalsIgnoreCase("/start")) {
      String username = user.getUsername();
      user.setUsername(username);

      botSender.sendMessage(chatId.toString(), ("Привіт! %s Це офіційний бот Милівської Територіальної громади. Цей бот допоможе вам:\n"
          + "* Оформити звернення до органів місцевої влади;").formatted(username));
      showMenu(chatId);

    } else if (text != null && text.equalsIgnoreCase("/menu")) {
      showMenu(chatId);
    } else if (text != null && text.equalsIgnoreCase("/application")) {
      handleStartApplication(chatId, user);
    } else if (text != null && text.equalsIgnoreCase("/info")) {
      showInfo(chatId);
    } else {
      botSender.sendMessage(chatId.toString(), "Будь ласка, надішліть /start, щоб почати.");
    }
  }

  private void showMenu(Long chatId) {
    botSender.sendMessage(chatId.toString(), "Оберіть дію", APPLICATION_NEW);
  }

  private void showInfo(Long chatId) {
    InlineKeyboardButton siteBtn = new InlineKeyboardButton();
    siteBtn.setText("Офіційний сайт");
    siteBtn.setUrl("https://miloverada.gov.ua/");

    InlineKeyboardButton aboutBtn = new InlineKeyboardButton();
    aboutBtn.setText("Про громаду");
    aboutBtn.setUrl("https://miloverada.gov.ua/about");

    InlineKeyboardButton newsBtn = new InlineKeyboardButton();
    newsBtn.setText("Новини громади");
    newsBtn.setUrl("https://miloverada.gov.ua/newsFeed/all");

    InlineKeyboardButton contactsBtn = new InlineKeyboardButton();
    contactsBtn.setText("Контакти");
    contactsBtn.setUrl("https://miloverada.gov.ua/contacts");

    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    keyboard.add(List.of(siteBtn, aboutBtn));
    keyboard.add(List.of(newsBtn, contactsBtn));
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    inlineKeyboardMarkup.setKeyboard(keyboard);


    botSender.sendMessage(chatId.toString(), "Милівська сільська об'єднана територіальна громада —  об'єднана територіальна громада  в Україні, в  Бериславському районі  Херсонської області\n"
        + "Утворена 15.07.2019", inlineKeyboardMarkup);
  }

  private void handleCompletion(Long chatId, UserData user) {
    user.setState(UserState.INITIAL);
    botSender.sendMessage(chatId.toString(), "Ваше звернення прийняте в обробку.");
    System.out.println("send user: " + user);
    applicationService.send(user);
  }

  private void handleStartApplication(Long chatId, UserData user) {
    botSender.sendMessage(chatId.toString(), "Щоб оформити звернення, будь ласка Введіть ваше ПІБ");
    user.setState(UserState.FULL_NAME_REQUESTED);
  }

  private void handleFullNameState(Long chatId, UserData userData, String text) {
    if (text.matches("^\\S+\\s+\\S+\\s+\\S+")) {
      userData.setUsername(text);
      botSender.sendMessage(chatId.toString(),
          "Будь ласка, поділіться своїм номером телефону.", true);
      userMap.get(chatId).setState(UserState.PHONE_REQUESTED);
    } else {
      botSender.sendMessage(chatId.toString(), "Будь ласка, правильно введіть ваше ПІБ");
    }
  }

  private void handlePhoneState(Long chatId, Update update) {
    if (update.getMessage().hasContact()) {
      String phoneNumber = update.getMessage().getContact().getPhoneNumber();
      userMap.get(chatId).setPhoneNumber(phoneNumber);
      ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
      replyKeyboardRemove.setRemoveKeyboard(true);

      botSender.sendMessage(chatId.toString(), "Отримано ваш номер телефону: " + phoneNumber, replyKeyboardRemove);
      botSender.sendMessage(chatId.toString(), "Тепер, будь ласка, введіть вашу електронну пошту. (або пропустіть)", EMAIL_SKIP_ADD);

      userMap.get(chatId).setState(UserState.EMAIL_REQUESTED);
    } else {
      botSender.sendMessage(chatId.toString(), "Будь ласка, поділіться своїм номером телефону.");
    }
  }

  private void handleEmailState(Long chatId, String text, UserData user) {
    if (text.matches("^\\S+@\\S+\\.\\S+$")) {
      user.setEmail(text);

      botSender.sendMessage(chatId.toString(),
          "Дякуємо! Ваша електронна пошта (" + text + ") була збережена. Тепер. введіть ваше звернення.");
      user.setState(UserState.APPLICATION_REQUEST);
    } else {
      botSender.sendMessage(chatId.toString(), "Будь ласка, введіть правильну електронну пошту.");
    }
  }

  private void handleApplicationState(Long chatId, String text, UserData user) {
    if (text != null && !text.isEmpty()) {
      botSender.sendMessage(chatId.toString(), "Ваше звернення отримано");
      user.setApplicationText(text);
      botSender.sendMessage(chatId.toString(), "Тепер, будь ласка, надішліть фото. (або пропустіть)", IMAGES_SKIP_ADD);
      user.setState(UserState.PHOTO_REQUEST);
    } else {
      botSender.sendMessage(chatId.toString(), "Будь ласка, введіть ваше звернення.");
    }
  }

  private void handlePhotoState(Long chatId, Update update, UserData userData) {
    if (update.getMessage().hasPhoto()) {
      System.out.println(update.getMessage().getPhoto());
      List<PhotoSize> photos = update.getMessage().getPhoto();
      System.out.println(photos);
      String fileId = update.getMessage().getPhoto().get(0).getFileId();

      File file = botSender.downloadPhoto(fileId);
      int number = userData.getPhotos().addPhoto(file);

      botSender.sendMessage(chatId.toString(),
          "Зображення %s/%s тримано!".formatted(number, MAX_PHOTOS_SIZE), IMAGES_STOP_ADD);

      if (!userData.getPhotos().isPlaceAvailable()) {
        userData.setState(UserState.COMPLETION);
        System.out.println("All images loaded");
      }
    } else {
      botSender.sendMessage(chatId.toString(),
          "Будь ласка, надішліть фото (якщо треба) або натисніть кнопку пропустити .",
          IMAGES_SKIP_ADD);
    }
  }

  private void handleCallbackData(Long chatId, String callbackData, UserData userData) {
    if (IMAGES_STOP_ADD_DATA.equals(callbackData) || IMAGES_SKIP_ADD_DATA.equals(callbackData)) {
      if (userData.isComplete()) {
        handleCompletion(chatId, userData);
      }
    } else if (EMAIL_SKIP_ADD_DATA.equals(callbackData)) {
      userData.setState(UserState.APPLICATION_REQUEST);
      botSender.sendMessage(chatId.toString(), "Будь ласка, введіть ваше звернення.");
    } else if (APPLICATION_NEW_DATA.equals(callbackData)) {
      handleStartApplication(chatId, userData);
    }
  }
}
