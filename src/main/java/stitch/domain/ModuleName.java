package stitch.domain;

import stitch.util.Validator;

public record ModuleName(String value) {
  public ModuleName {
    value = Validator.requireNotBlank(value, "Module name cannot be null or empty.");
  }

  @Override
  public String toString() {
    return value;
  }
}

