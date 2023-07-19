package gov.miloverada.telegram_bot.interfaces;

import gov.miloverada.telegram_bot.domain.Service;

import java.util.List;

public interface ServiceRepository {

    List<Service> getAll();

    Service findById(String id);
}
