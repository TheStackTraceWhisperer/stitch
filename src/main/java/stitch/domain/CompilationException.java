package stitch.domain;

public final class CompilationException extends BuildException {
  public CompilationException(String message) {
    this(message, null);
  }

  public CompilationException(String message, Throwable cause) {
    super(message, cause);
  }
}

