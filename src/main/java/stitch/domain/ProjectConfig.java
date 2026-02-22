package stitch.domain;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public record ProjectConfig(
  String projectName,
  Path sourceDir,
  Optional<String> moduleNameOverride,
  Optional<String> mainClass,
  Map<String, String> moduleMappings
) {
  public ProjectConfig {
    if (projectName == null || projectName.isBlank()) {
      throw new ModuleResolutionException("Project name cannot be null or empty.");
    }
    if (sourceDir == null) {
      throw new ModuleResolutionException("Source directory cannot be null.");
    }
    moduleMappings = moduleMappings == null ? Map.of() : Map.copyOf(moduleMappings);
  }
}