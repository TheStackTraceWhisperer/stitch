package stitch.domain;

public class ProjectContext {
    private final ProjectConfig config;
    private ParsedModule module;

    public ProjectContext(ProjectConfig config) {
        this.config = config;
    }

    public ProjectConfig config() { return config; }
    public ParsedModule module() { return module; }
    public void setModule(ParsedModule module) { this.module = module; }
}

