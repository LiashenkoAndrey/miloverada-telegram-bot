package gov.miloverada.telegram_bot;

import gov.miloverada.telegram_bot.util.UserCash;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Saves users cash in {@link java.util.HashMap}
 */
@Component
public class UserCashService {

    private final Map<Long, UserCash> cashMap = new HashMap<>();


    /**
     * Returns user cash by telegram id.
     * If there is no such a key it puts a new record where key is telegram id and value {@link UserCash}
     * @param telegramId key
     * @return value
     */
    public UserCash getUserCash(Long telegramId) {
        if (cashMap.containsKey(telegramId)) {
            return cashMap.get(telegramId);
        } else {
            UserCash cash = new UserCash();
            cashMap.put(telegramId, cash);
            return cash;
        }
    };

}

