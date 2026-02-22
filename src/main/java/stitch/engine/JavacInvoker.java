package stitch.engine;

import stitch.domain.CompilationException;
import stitch.domain.ParsedModule;
import stitch.domain.ProjectConfig;

import javax.tools.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class JavacInvoker {
  public static void compile(ProjectConfig config, ParsedModule module) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    try {
      Path classesRoot = Path.of("target/classes");
      Path outputDir = classesRoot.resolve(config.projectName());
      Files.createDirectories(outputDir);

      List<String> options = new ArrayList<>(List.of(
        "--enable-preview",
        "--release", "25",
        "-d", outputDir.toString(),
        // Dynamically use the module name instead of hardcoding "stitch="
        "--module-source-path", module.name().value() + "=" + config.sourceDir().toString(),
        "--module-path", classesRoot.toString()
      ));

      List<Path> sources = Files.walk(config.sourceDir())
        .filter(p -> p.toString().endsWith(".java"))
        .toList();

      System.out.println("📝 [" + config.projectName() + "] Found " + sources.size() + " source files.");

      Iterable<? extends JavaFileObject> compilationUnits =
        fileManager.getJavaFileObjectsFromPaths(sources);

      JavaCompiler.CompilationTask task = compiler.getTask(
        null, fileManager, null, options, null, compilationUnits);

      if (!task.call()) {
        throw new CompilationException("Compilation failed for project: " + config.projectName());
      }
    } catch (IOException e) {
      throw new CompilationException("Failed to initialize compiler output paths", e);
    }
  }
}