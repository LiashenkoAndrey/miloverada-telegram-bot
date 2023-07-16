package gov.miloverada.telegram_bot.impl;

import gov.miloverada.telegram_bot.UpdateReceiver;
import gov.miloverada.telegram_bot.interfaces.UpdateService;
import gov.miloverada.telegram_bot.exceptions.UpdateProcessingException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
public class UpdateServiceImpl implements UpdateService {

    private static final Logger logger = LogManager.getLogger(UpdateServiceImpl.class);

    public final ExecutorService executorService;
    private final UpdateReceiver updateReceiver;

    @Override
    public void processUpdate(Update update) throws UpdateProcessingException  {
        executorService.execute(() -> {
            try {
                updateReceiver.processUpdate(update);
            } catch (UpdateProcessingException e) {
                logger.error(e.getMessage());
                throw new UpdateProcessingException(e);
            }
        });
    }
}
