package gov.miloverada.telegram_bot.controllers;

import gov.miloverada.telegram_bot.controllers.exceptions.CallBackControllerException;
import gov.miloverada.telegram_bot.domain.Service;
import gov.miloverada.telegram_bot.interfaces.ServiceRepository;
import gov.miloverada.telegram_bot.util.annotations.CallBack;
import gov.miloverada.telegram_bot.util.annotations.Controller;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static gov.miloverada.telegram_bot.controllers.ControllerUtils.createRowBtn;

@Controller
@RequiredArgsConstructor
@Component
public class ServiceCallbacks extends AbsController {

    private final ServiceRepository serviceRepository;

    private static final Logger logger = LogManager.getLogger(ServiceCallbacks.class);


    @CallBack
    public void showServices() {
        try {

            List<Service> l = serviceRepository.getAll();
            List<List<InlineKeyboardButton>> list = new ArrayList<>();

            // split list to sub lists (2 objects per list)
            for (int i = 0; i < l.size(); i= i+2) {
                if (i+1 < l.size()) {
                    list.add(List.of(
                            createRowBtn(l.get(i).getName(), "showServiceDetails?" + l.get(i).getId()),
                            createRowBtn(l.get(i+2).getName(), "showServiceDetails?"+ l.get(i+2).getId())
                    ));
                } else if (i != l.size()){
                    list.add((List.of(
                            createRowBtn(l.get(i).getName(), "showServiceDetails?"+ l.get(i).getId())
                    )));
                }
            }

            bot.execute(EditMessageText.builder()
                    .chatId(chatId())
                    .messageId(cash().getLastBotMessageId())
                    .text("Оберіть послугу")
                    .replyMarkup(new InlineKeyboardMarkup(list))
                    .build());

        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new CallBackControllerException(e);
        }
    }

    @CallBack
    public void showServiceDetails(String id) {
        try {

            Service service = serviceRepository.findById(id);

            String text = "<b>" + service.getName() + "</b>\n" +
                    service.getDescription();

            bot.execute(EditMessageText.builder()
                            .chatId(chatId())
                            .messageId(cash().getLastBotMessageId())
                            .text(text)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(new InlineKeyboardMarkup(List.of(
                                    List.of(
                                       createRowBtn("<-- назад", "showServices"),
                                       createRowBtn("Записатися", "createRecord?" + id)
                                    )
                            )))
                    .build());

        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new CallBackControllerException(e);
        }
    }
}
