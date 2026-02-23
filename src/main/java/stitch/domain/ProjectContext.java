package stitch.domain;

import java.nio.file.Path;
import java.util.List;

public class ProjectContext {
    private final ProjectConfig config;
    private ParsedModule module;
    private List<Path> resolvedDependencies = List.of();

    public ProjectContext(ProjectConfig config) {
        this.config = config;
    }

    public ProjectConfig config() { return config; }
    public ParsedModule module() { return module; }
    public void setModule(ParsedModule module) { this.module = module; }

    public List<Path> resolvedDependencies() { return resolvedDependencies; }
    public void setResolvedDependencies(List<Path> resolvedDependencies) {
        this.resolvedDependencies = resolvedDependencies;
    }
}
