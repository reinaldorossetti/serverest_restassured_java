package dev.serverest.utils;

import java.util.Locale;
import net.datafaker.Faker;

/** Utility class to provide random test data using Java Faker. */
public final class FakerUtils {
  public static final Faker FAKER = new Faker();

  private FakerUtils() {}

  /** Generates a random full name. */
  public static String randomName() {
    return FAKER.name().fullName();
  }

  /** Generates a unique product name using nanoTime. */
  public static String randomProduct() {
    return FAKER.commerce().productName() + " " + System.nanoTime();
  }

  /** Generates a unique lowercase email address. */
  public static String randomEmail() {
    return FAKER.name().firstName().toLowerCase(Locale.ROOT)
        + "."
        + System.nanoTime()
        + "@gmail.com";
  }

  /** Generates a random password between 8 and 16 characters. */
  public static String randomPassword() {
    return FAKER.credentials().password(8, 16);
  }

  /**
   * Generates a random alphanumeric ID with 16 characters (matching ServeRest ID format).
   * Used for testing edge cases like PUT with non-existent IDs.
   */
  public static String randomAlphanumericId() {
    return FAKER.bothify("????????????????");
  }
}
