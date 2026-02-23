package stitch;

import stitch.domain.ProjectConfig;
import stitch.engine.ConcurrentEngine;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Workspace {
  private final String name;
  private final Map<String, ProjectConfig> projects = new HashMap<>();
  private final Map<String, String> stitchedModules = new HashMap<>(); // Store Maven coordinates

  private Workspace(String name) {
    this.name = name;
  }

  public static Workspace init(String name) {
    return new Workspace(name);
  }

  public Workspace stitch(String moduleName, String coordinates) {
    this.stitchedModules.put(moduleName, coordinates);
    return this;
  }

  public ProjectBuilder project(String projectName) {
    return new ProjectBuilder(this, projectName);
  }

  public void execute() {
    ConcurrentEngine.execute(projects);
  }

  // Inside Workspace.java -> ProjectBuilder class
  public static class ProjectBuilder {
    private final Workspace workspace;
    private final String projectName;
    // Update the default path here
    private Path sourceDir = Path.of("src/main/java");
    private String moduleName;
    private String mainClass;

    ProjectBuilder(Workspace workspace, String projectName) {
      this.workspace = workspace;
      this.projectName = projectName;
    }

    public ProjectBuilder moduleName(String name) {
      this.moduleName = name;
      return this;
    }

    public ProjectBuilder sourceDir(String path) {
      this.sourceDir = Path.of(path);
      return this;
    }

    public ProjectBuilder mainClass(String mainClass) {
      this.mainClass = mainClass;
      return this;
    }

    public Workspace execute() {
      workspace.projects.put(projectName, new ProjectConfig(
        projectName,
        sourceDir,
        Optional.ofNullable(moduleName),
        Optional.ofNullable(mainClass),
        workspace.stitchedModules
      ));
      workspace.execute();
      return workspace;
    }
  }
}