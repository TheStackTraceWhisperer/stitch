package stitch.engine;

import stitch.domain.ParsedModule;
import stitch.domain.ProjectConfig;
import stitch.resolve.ModuleInfoParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class ConcurrentEngine {
  public static void execute(Map<String, ProjectConfig> projects) {
    BuildGraph graph = createGraph(projects);
    executeTasks(projects, graph);
  }

  private static BuildGraph createGraph(Map<String, ProjectConfig> projects) {
    BuildGraph graph = new BuildGraph();
    projects.keySet().forEach(graph::register);
    return graph;
  }

  private static void executeTasks(Map<String, ProjectConfig> projects, BuildGraph graph) {
    System.out.println("🚀 Starting Stitch build engine with " + projects.size() + " projects...");
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      projects.values().forEach(config -> executor.submit(() -> {
        System.out.println("🧵 [Thread] Starting task for: " + config.projectName());
        buildProject(config, graph);
      }));
    }
    System.out.println("🏁 Build process completed.");
  }

  private static void buildProject(ProjectConfig config, BuildGraph graph) {
    try {
      Path moduleInfoPath = config.sourceDir().resolve("module-info.java");

      if (!Files.exists(moduleInfoPath)) {
        throw new stitch.domain.ModuleResolutionException(
          "Missing module-info.java at " + moduleInfoPath.toAbsolutePath() +
            ". Ensure your project follows the Maven layout or configure sourceDir()."
        );
      }

      ParsedModule parsed = ModuleInfoParser.parse(moduleInfoPath);

      stitch.domain.ModuleName effectiveName = config.moduleNameOverride()
        .map(stitch.domain.ModuleName::new)
        .orElse(parsed.name());

      ParsedModule effectiveModule = new ParsedModule(effectiveName, parsed.requires());

      System.out.println("⏳ [" + config.projectName() + "] Awaiting dependencies: " + effectiveModule.requires());
      awaitDependencies(effectiveModule, graph);

      System.out.println("🔨 [" + config.projectName() + "] Compiling...");
      JavacInvoker.compile(config, effectiveModule);

      // Handle Resources - Ensure they go into the inner module directory too!
      Path resourceSrc = config.sourceDir().resolve("../../resources"); // src/main/resources
      Path resourceTarget = Path.of("target/classes").resolve(config.projectName());

      ResourceInvoker.copy(resourceSrc, resourceTarget);

      // Pass the effectiveModule to JarInvoker
      JarInvoker.packageProject(config, effectiveModule); // <--- Updated call

      System.out.println("✅ [" + config.projectName() + "] Finished.");

      graph.markComplete(config.projectName());
    } catch (Exception e) {
      System.err.println("❌ [" + config.projectName() + "] Failed: " + e.getMessage());
      graph.markFailed(config.projectName(), e);
      throw new RuntimeException(e);
    }
  }

  private static void awaitDependencies(ParsedModule module, BuildGraph graph) {
    module.requires().forEach(req ->
      graph.getDependencyTask(req.value()).ifPresent(CompletableFuture::join));
  }
}

