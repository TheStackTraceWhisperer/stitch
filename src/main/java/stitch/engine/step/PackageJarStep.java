package stitch.engine.step;

import stitch.domain.ProjectContext;
import stitch.util.Logger;

import java.nio.file.*;
import java.util.*;
import java.util.spi.ToolProvider;

public class PackageJarStep implements BuildStep {
    @Override
    public void execute(ProjectContext context) {
        ToolProvider jar = ToolProvider.findFirst("jar").orElseThrow(() -> new RuntimeException("jar tool not found"));

        String projectName = context.config().projectName();
        Path outputJar = Path.of("target").resolve(projectName + ".jar");
        Path classesDir = Path.of("target/classes").resolve(projectName).resolve(context.module().name().value());

        List<String> args = new ArrayList<>(List.of("--create", "--file", outputJar.toString()));
        context.config().mainClass().ifPresent(main -> {
            args.add("--main-class");
            args.add(main);
        });
        args.addAll(List.of("-C", classesDir.toString(), "."));

        if (jar.run(System.out, System.err, args.toArray(String[]::new)) != 0) {
            throw new RuntimeException("Failed to create JAR for " + projectName);
        }
        Logger.info("📦 [" + projectName + "] Created JAR: " + outputJar);
    }
}
