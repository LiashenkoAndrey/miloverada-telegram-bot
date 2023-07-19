package gov.miloverada.telegram_bot.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.miloverada.telegram_bot.domain.Service;
import gov.miloverada.telegram_bot.exceptions.HttpGetException;
import gov.miloverada.telegram_bot.exceptions.RepositoryException;
import gov.miloverada.telegram_bot.interfaces.ServiceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static gov.miloverada.telegram_bot.util.HttpHelper.getJson;

@Component
public class ServiceRepositoryImpl implements ServiceRepository {

    private static final Logger logger = LogManager.getLogger(ServiceRepositoryImpl.class);

    private static final String HOST = "http://localhost";

    @Override
    public List<Service> getAll() {
        try {
            String resp = getJson(HOST, "/services/all/json");
            return new ObjectMapper().readValue(resp, new TypeReference<>() { });
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Service findById(String id) {
       try {
           String resp = getJson(HOST, "/services/"+ id +"/json");
           if (resp != null) {
               return new ObjectMapper().readValue(resp, new TypeReference<>() { });
           } else {
               return null;
           }
       } catch (JsonProcessingException | HttpGetException e) {
           logger.error(e.getMessage());
           throw new RepositoryException(e);
       }
    }

}
