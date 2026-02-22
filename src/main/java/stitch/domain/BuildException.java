package stitch.domain;

public sealed class BuildException extends RuntimeException
  permits ModuleResolutionException, CompilationException {
  protected BuildException(String message, Throwable cause) {
    super(message, cause);
  }
}

