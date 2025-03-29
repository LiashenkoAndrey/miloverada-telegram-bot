package ua.gov.miloverada.telegrambot.service;

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.gov.miloverada.telegrambot.model.UserData;

/**
 * @author Liashenko Andrii
 * @since 3/29/2025
 */
@Service
public class ApplicationService {

  @Value("${api.server.send-application-endpoint}")
  private String API_SERVER_URL;

  public void send(UserData userData) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost httpPost = new HttpPost(API_SERVER_URL);
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.setMode(HttpMultipartMode.LEGACY);

      builder.addTextBody("fullName", userData.getUsername(), ContentType.TEXT_PLAIN);
      builder.addTextBody("phoneNumber", userData.getPhoneNumber(), ContentType.TEXT_PLAIN);

      if (userData.getEmail() != null) {
        builder.addTextBody("email", userData.getEmail(), ContentType.TEXT_PLAIN);
      }

      if (!userData.getPhotos().isEmpty()) {
        userData.getPhotos().getImageFiles().forEach(photo ->
            builder.addBinaryBody("files", photo, ContentType.DEFAULT_BINARY, photo.getName())
        );
      }

      builder.addTextBody("applicationText", userData.getApplicationText(), ContentType.TEXT_PLAIN);
      builder.addTextBody("tempNotificationDestination", "/none", ContentType.TEXT_PLAIN);


      HttpEntity multipart = builder.build();
      httpPost.setEntity(multipart);

      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
        System.out.println("Response Code: " + response.getCode());
        System.out.println("Response: " + response.getEntity().toString());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
