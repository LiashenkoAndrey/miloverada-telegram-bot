package ua.gov.miloverada.telegrambot.model;

import static ua.gov.miloverada.telegrambot.Constants.MAX_PHOTOS_SIZE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liashenko Andrii
 * @since 3/29/2025
 */
public class ApplicationPhotos {

  private List<File> imageFiles = new ArrayList<>();

  public boolean isPlaceAvailable() {
    return imageFiles.size() < MAX_PHOTOS_SIZE;
  }

  public boolean isEmpty() {
    return imageFiles.isEmpty();
  }

  /**
   * @param file photo file
   * @return photo number
   */
  public int addPhoto(File file) {
    if (isPlaceAvailable()) {
      imageFiles.add(file);

      return imageFiles.size();
    } else {
      throw new IllegalStateException("Not enough size, max is " + MAX_PHOTOS_SIZE);
    }
  }

  public List<File> getImageFiles() {
    return imageFiles;
  }
}
