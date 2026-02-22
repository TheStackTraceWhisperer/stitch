package stitch.domain;

public final class ModuleResolutionException extends BuildException {
  public ModuleResolutionException(String message) {
    this(message, null);
  }

  public ModuleResolutionException(String message, Throwable cause) {
    super(message, cause);
  }
}

