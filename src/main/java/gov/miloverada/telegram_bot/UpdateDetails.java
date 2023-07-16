package gov.miloverada.telegram_bot;

import gov.miloverada.telegram_bot.util.UserCash;
import gov.miloverada.telegram_bot.util.annotations.Controller;
import lombok.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Getter
@Setter
@Component
@ToString
@RequiredArgsConstructor
public class UpdateDetails {

    private final UserCashService cashService;

    private Long chatId;

    private User user;

    private Message message;

    private Integer messageId;

    private UserCash cash;

    private Update update;


    public void update(Update update) {
        Message message;

        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
            this.cash = cashService.getUserCash(update.getCallbackQuery().getFrom().getId());

        } else {
            message = update.getMessage();
            this.cash = cashService.getUserCash(update.getMessage().getFrom().getId());
        }

        this.update = update;
        this.message = message;
        this.user = message.getFrom();
        this.messageId = message.getMessageId();
        this.chatId = message.getChatId();

    };
}