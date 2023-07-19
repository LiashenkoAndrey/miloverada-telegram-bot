package gov.miloverada.telegram_bot.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Record {

    private String firstName;

    private String lastName;

    private String surname;

    private String phoneNumber;

    private String note;

    private String serviceId;

    private String dateOfVisit;

    private String timeOfVisit;

    private String telegramId;
}
