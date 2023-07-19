package gov.miloverada.telegram_bot.interfaces;

import gov.miloverada.telegram_bot.domain.Record;
import gov.miloverada.telegram_bot.exceptions.RepositoryException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public interface RecordRepository {

    String saveRecord(Record record) throws RepositoryException;

    List<LocalTime> findRecordsByDate(String serviceId, String date);

    List<Record> getRecordsByTelegramId(String telegramId);
}
