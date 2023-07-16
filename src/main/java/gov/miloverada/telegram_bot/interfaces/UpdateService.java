package gov.miloverada.telegram_bot.interfaces;


import gov.miloverada.telegram_bot.exceptions.UpdateProcessingException;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateService {

    void processUpdate(Update update) throws UpdateProcessingException;
}
