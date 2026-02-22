package stitch.resolve;

import stitch.domain.ModuleName;
import stitch.domain.ModuleResolutionException;
import stitch.domain.ParsedModule;
import stitch.util.Regex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModuleInfoParser {
  private static final Pattern MODULE_PATTERN = Pattern.compile("module\\s+(?<moduleName>[\\w.]+)\\s*\\{");
  private static final Pattern REQUIRES_PATTERN = Pattern.compile("requires\\s+(?:transitive\\s+)?(?:static\\s+)?(?<moduleName>[\\w.]+)\\s*;");
  private static final String GROUP_MODULE_NAME = "moduleName";

  public static ParsedModule parse(Path moduleInfoPath) {
    String raw = readFile(moduleInfoPath);
    String clean = sanitizeContent(raw);

    ModuleName name = extractModuleName(clean, moduleInfoPath);
    Set<ModuleName> requires = extractDependencies(clean);

    return new ParsedModule(name, requires);
  }

  private static String sanitizeContent(String raw) {
    return stripLineComments(stripBlockComments(raw));
  }

  private static String readFile(Path path) {
    try {
      return Files.readString(path);
    } catch (IOException e) {
      throw new ModuleResolutionException("Failed to read: " + path, e);
    }
  }

  private static String stripBlockComments(String content) {
    return content.replaceAll("(?s)/\\*.*?\\*/", "");
  }

  private static String stripLineComments(String content) {
    return content.replaceAll("//.*", "");
  }

  private static ModuleName extractModuleName(String content, Path path) {
    return Regex.extractAllMatches(MODULE_PATTERN, content).stream()
      .findFirst()
      .map(match -> match.get(GROUP_MODULE_NAME))
      .map(ModuleName::new)
      .orElseThrow(() -> new ModuleResolutionException("No module declaration in: " + path));
  }

  private static Set<ModuleName> extractDependencies(String content) {
    return Regex.extractAllMatches(REQUIRES_PATTERN, content).stream()
      .map(match -> match.get(GROUP_MODULE_NAME))
      .filter(name -> name != null)
      .map(ModuleName::new)
      .collect(Collectors.toUnmodifiableSet());
  }
}

