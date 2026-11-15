package dev.serverest.utils;

import java.util.Locale;
import net.datafaker.Faker;

/** Utility class to provide random test data using Java Faker. */
public final class FakerUtils {
  public static final Faker FAKER = new Faker();

  private FakerUtils() {
    // Utility class
  }

  public static String randomName() {
    return FAKER.name().fullName();
  }

  public static String randomProduct() {
    return FAKER.commerce().productName() + " " + System.nanoTime();
  }

  public static String randomEmail() {
    return FAKER.name().firstName().toLowerCase(Locale.ROOT)
        + "."
        + System.nanoTime()
        + "@gmail.com";
  }

  public static String randomPassword() {
    return FAKER.credentials().password(8, 16);
  }
}
