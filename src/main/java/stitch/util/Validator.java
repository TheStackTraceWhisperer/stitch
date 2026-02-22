package stitch.util;

import stitch.domain.ModuleResolutionException;

public final class Validator {
  private Validator() {
  }

  public static String requireNotBlank(String value, String errorMessage) {
    if (value == null || value.isBlank()) {
      throw new ModuleResolutionException(errorMessage);
    }
    return value.trim();
  }
}

