package stitch.engine.step;

import stitch.domain.*;
import stitch.util.Logger;

import javax.tools.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class CompileStep implements BuildStep {
    @Override
    public void execute(ProjectContext context) {
        ProjectConfig config = context.config();

        Logger.info("🔨 [" + config.projectName() + "] Compiling...");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        try {
            Path classesRoot = Path.of("target/classes");
            Path outputDir = classesRoot.resolve(config.projectName());
            Files.createDirectories(outputDir);

            List<String> options = List.of(
                "--enable-preview", "--release", "25",
                "-d", outputDir.toString(),
                "--module-source-path", context.module().name().value() + "=" + config.sourceDir().toString(),
                "--module-path", classesRoot.toString()
            );

            List<Path> sources = Files.walk(config.sourceDir()).filter(p -> p.toString().endsWith(".java")).toList();
            Logger.info("📝 [" + config.projectName() + "] Found " + sources.size() + " source files.");

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileManager.getJavaFileObjectsFromPaths(sources));

            if (!task.call()) throw new CompilationException("Compilation failed for project: " + config.projectName());
        } catch (IOException e) {
            throw new CompilationException("Failed to initialize compiler paths", e);
        }
    }
}
