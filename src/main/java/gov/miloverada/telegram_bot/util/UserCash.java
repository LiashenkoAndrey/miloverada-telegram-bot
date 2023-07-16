package gov.miloverada.telegram_bot.util;

import gov.miloverada.telegram_bot.domain.UserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains cash data of user
 */
@Setter
@ToString
@Getter
public class UserCash {

    private UserDto user = new UserDto();

    /**
     * If this field is not null it will execute method with name decided in this field
     */
    private String currentMethod;

    private Integer lastMessageId;

    private List<Integer> lastUserMessages = new ArrayList<>();

    public boolean hasCurrentMethod() {
        return currentMethod != null;
    }

    public void addLastUserMessage(Message message) {
        lastUserMessages.add(message.getMessageId());
    }
}
