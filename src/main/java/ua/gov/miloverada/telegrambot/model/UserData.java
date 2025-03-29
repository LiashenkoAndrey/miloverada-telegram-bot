package ua.gov.miloverada.telegrambot.model;

import static java.util.Objects.nonNull;

/**
 * @author Liashenko Andrii
 * @since 3/29/2025
 */
public class UserData {

  private String username;

  private String phoneNumber;

  private String email;

  private String applicationText;

  private UserState state;

  private ApplicationPhotos photos = new ApplicationPhotos();

  public ApplicationPhotos getPhotos() {
    return photos;
  }

  public boolean isComplete() {
    return nonNull(username) && nonNull(phoneNumber) && nonNull(applicationText);
  }

  public void setPhotos(ApplicationPhotos photos) {
    this.photos = photos;
  }

  @Override
  public String toString() {
    return "UserData{" +
        "username='" + username + '\'' +
        ", phoneNumber='" + phoneNumber + '\'' +
        ", email='" + email + '\'' +
        ", state=" + state +
        '}';
  }

  public String getApplicationText() {
    return applicationText;
  }

  public void setApplicationText(String applicationText) {
    this.applicationText = applicationText;
  }

  public UserData() {
    this.state = UserState.INITIAL;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserState getState() {
    return state;
  }

  public void setState(UserState state) {
    this.state = state;
  }
}
