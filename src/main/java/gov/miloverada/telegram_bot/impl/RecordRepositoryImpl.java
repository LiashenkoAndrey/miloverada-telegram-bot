package gov.miloverada.telegram_bot.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.miloverada.telegram_bot.domain.Record;
import gov.miloverada.telegram_bot.exceptions.RepositoryException;
import gov.miloverada.telegram_bot.interfaces.RecordRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.List;

import static gov.miloverada.telegram_bot.util.HttpHelper.buildQuery;
import static gov.miloverada.telegram_bot.util.HttpHelper.sendAndGetStringAsResp;

@Component
public class RecordRepositoryImpl implements RecordRepository {

    private static final Logger logger = LogManager.getLogger(RecordRepositoryImpl.class);

    @Override
    public String saveRecord(Record record) throws RepositoryException {
        try {
            String bodyJson = new ObjectMapper().writeValueAsString(record);

            HttpResponse<String> response = sendAndGetStringAsResp(HttpRequest.newBuilder()
                    .uri(buildQuery("/queue/record/new"))
                    .headers("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build());

            return response.body();
        } catch (IOException | RepositoryException e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<LocalTime> findRecordsByDate(String serviceId, String date) {
        try {

            HttpResponse<String> response = sendAndGetStringAsResp(
                    HttpRequest.newBuilder()
                            .uri(buildQuery("/queue/record/find","serviceId", serviceId, "date", date))
                            .GET()
                            .build()
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.readValue(response.body(), new TypeReference<>() { });
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<Record> getRecordsByTelegramId(String telegramId) {
        try {

            HttpResponse<String> response = sendAndGetStringAsResp(
                    HttpRequest.newBuilder()
                    .uri(buildQuery("/queue/record/findUserRecords", "telegramId", telegramId))
                    .GET()
                    .build()
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.readValue(response.body(), new TypeReference<>() { });

        }catch (IOException e) {
            logger.error(e.getMessage());
            throw new RepositoryException(e);
        }
    }


}
