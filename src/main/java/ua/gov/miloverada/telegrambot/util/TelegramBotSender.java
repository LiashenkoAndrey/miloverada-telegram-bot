package ua.gov.miloverada.telegrambot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Liashenko Andrii
 * @since 3/29/2025
 */
@Component
public class TelegramBotSender extends DefaultAbsSender {

  public static final String IMAGES_STOP_ADD_DATA = "photo_stop_add";
  public static final String IMAGES_SKIP_ADD_DATA = "photo_skip";
  public static final String EMAIL_SKIP_ADD_DATA = "email_skip";
  public static final String APPLICATION_NEW_DATA = "application_new";

  public static final InlineKeyboardMarkup APPLICATION_NEW = createSkipInlineBtn("Оформити звернення", "application_new");
  public static final InlineKeyboardMarkup IMAGES_STOP_ADD = createSkipInlineBtn("Завершити додавання", "photo_stop_add");

  public static final InlineKeyboardMarkup IMAGES_SKIP_ADD = createSkipInlineBtn("Пропустити додавання фото", "photo_skip");

  public static final InlineKeyboardMarkup EMAIL_SKIP_ADD = createSkipInlineBtn("Пропустити додавання пошти", "email_skip");

  protected TelegramBotSender() {
    super(new DefaultBotOptions());
  }
  @Value("${telegram.bot.token}")
  private String botToken;


  @Override
  public String getBotToken() {
    return botToken;
  }

  public void sendMessage(String chatId, String text, boolean requestPhone) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(text);

    if (requestPhone) {
      ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
      keyboardMarkup.setResizeKeyboard(true);

      KeyboardRow row = new KeyboardRow();
      KeyboardButton button = new KeyboardButton("📞 Share Phone Number");
      button.setRequestContact(true);

      row.add(button);
      keyboardMarkup.setKeyboard(List.of(row));

      message.setReplyMarkup(keyboardMarkup);
    }

    try {
      execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  public File downloadPhoto(String fileId) {
    try {
      GetFile getFile = GetFile.builder()
          .fileId(fileId)
          .build();

      String filePath = execute(getFile).getFilePath();

      return downloadFile(filePath);

    } catch (TelegramApiException e) {
      throw new RuntimeException("Photo download error: " + fileId, e);
    }
  }

  public void sendMessage(String chatId, String text) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(text);

    try {
      execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(String chatId, String text, ReplyKeyboard markup) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(text);
    message.setReplyMarkup(markup);
    try {
      execute(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  public static InlineKeyboardMarkup createSkipInlineBtn(String text, String data) {
    InlineKeyboardButton button = new InlineKeyboardButton();
    button.setText(text);
    button.setCallbackData(data);

    List<InlineKeyboardButton> row = new ArrayList<>();
    row.add(button);
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    keyboard.add(row);

    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
    inlineKeyboard.setKeyboard(keyboard);
    return inlineKeyboard;
  }
}
