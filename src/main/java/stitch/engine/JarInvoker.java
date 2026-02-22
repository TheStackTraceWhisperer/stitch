package stitch.engine;

import stitch.domain.ParsedModule; // Added import
import stitch.domain.ProjectConfig;
import java.nio.file.*;
import java.util.*;
import java.util.spi.ToolProvider;

public class JarInvoker {
  // Pass the module in so we know the sub-folder name
  public static void packageProject(ProjectConfig config, ParsedModule module) {
    ToolProvider jar = ToolProvider.findFirst("jar")
      .orElseThrow(() -> new RuntimeException("jar tool not found"));

    Path outputJar = Path.of("target").resolve(config.projectName() + ".jar");

    // LOOK ONE DIRECTORY DEEPER (into the module-named subfolder)
    Path classesDir = Path.of("target/classes")
      .resolve(config.projectName())
      .resolve(module.name().value());

    if (!Files.exists(classesDir)) {
      throw new RuntimeException("Cannot package JAR: classes directory missing at " + classesDir);
    }

    List<String> args = new ArrayList<>();
    args.add("--create");
    args.add("--file");
    args.add(outputJar.toString());

    config.mainClass().ifPresent(main -> {
      args.add("--main-class");
      args.add(main);
    });

    args.add("-C");
    args.add(classesDir.toString());
    args.add(".");

    int result = jar.run(System.out, System.err, args.toArray(new String[0]));

    if (result != 0) {
      throw new RuntimeException("Failed to create JAR for " + config.projectName());
    }
    System.out.println("📦 [" + config.projectName() + "] Created JAR: " + outputJar);
  }
}