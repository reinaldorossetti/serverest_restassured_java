package dev.serverest.utils;

import net.datafaker.Faker;

/** Utility class to provide random test data using Java Faker. */
public class FakerUtils {
  public static final Faker FAKER = new Faker();

  public static String randomName() {
    return FAKER.name().fullName();
  }

  public static String randomProduct() {
    return FAKER.commerce().productName() + " " + System.nanoTime();
  }

  public static String randomEmail() {
    return FAKER.name().firstName().toLowerCase() + "." + System.nanoTime() + "@gmail.com";
  }

  public static String randomPassword() {
    return FAKER.internet().password(8, 16);
  }
}
