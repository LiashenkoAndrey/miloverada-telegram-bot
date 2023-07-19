package gov.miloverada.telegram_bot;

import gov.miloverada.telegram_bot.exceptions.BotStartupException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class Runner {

    @Bean
    private ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    private static final Logger logger = LogManager.getLogger(Bot.class);

    public static void main(String[] args) throws BotStartupException {
        ApplicationContext context = new AnnotationConfigApplicationContext("gov.miloverada.telegram_bot");

        Bot bot = context.getBean(Bot.class);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            logger.info("Application started");
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.fatal(e.getMessage());
            throw new BotStartupException(e);
        }

    }


}
