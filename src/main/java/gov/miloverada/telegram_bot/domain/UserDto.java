package gov.miloverada.telegram_bot.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

    private String FullNameWithSurname;

    private String phoneNumber;

}
