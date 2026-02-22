package stitch.domain;

import java.util.Objects;
import java.util.Set;

public record ParsedModule(ModuleName name, Set<ModuleName> requires) {
  public ParsedModule {
    Objects.requireNonNull(name, "Parsed module must have a valid ModuleName.");
    requires = Set.copyOf(requires);
  }

  public boolean requiresModule(ModuleName targetName) {
    return requires.contains(targetName);
  }
}

