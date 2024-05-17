package com.gci.schedule.driverless.util;

import static java.lang.String.format;

/**
 * Utilities, typically copied in from guava, so as to avoid dependency conflicts.
 */
public class Util {


  private Util() { // no instances
  }

  /**
   * Copy of {@code com.google.common.base.Preconditions#checkArgument}.
   */
  public static void checkArgument(boolean expression,
                                   String errorMessageTemplate,
                                   Object... errorMessageArgs) {
    if (!expression) {
      throw new IllegalArgumentException(
          format(errorMessageTemplate, errorMessageArgs));
    }
  }

  /**
   * Copy of {@code com.google.common.base.Preconditions#checkNotNull}.
   */
  public static <T> T checkNotNull(T reference,
                                   String errorMessageTemplate,
                                   Object... errorMessageArgs) {
    if (reference == null) {
      // If either of these parameters is null, the right thing happens anyway
      throw new NullPointerException(
          format(errorMessageTemplate, errorMessageArgs));
    }
    return reference;
  }

  /**
   * Copy of {@code com.google.common.base.Preconditions#checkState}.
   */
  public static void checkState(boolean expression,
                                String errorMessageTemplate,
                                Object... errorMessageArgs) {
    if (!expression) {
      throw new IllegalStateException(
          format(errorMessageTemplate, errorMessageArgs));
    }
  }

  /**
   * Adapted from {@code com.google.common.base.Strings#emptyToNull}.
   */
  public static String emptyToNull(String string) {
    return string == null || string.isEmpty() ? null : string;
  }

  /**
   * If the provided String is not null or empty.
   *
   * @param value to evaluate.
   * @return true of the value is not null and not empty.
   */
  public static boolean isNotBlank(String value) {
    return value != null && !value.isEmpty();
  }

  /**
   * If the provided String is null or empty.
   *
   * @param value to evaluate.
   * @return true if the value is null or empty.
   */
  public static boolean isBlank(String value) {
    return value == null || value.isEmpty();
  }
}
