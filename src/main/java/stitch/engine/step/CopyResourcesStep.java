package stitch.engine.step;

import stitch.domain.ProjectContext;
import stitch.util.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class CopyResourcesStep implements BuildStep {
    @Override
    public void execute(ProjectContext context) {
        Path source = context.config().sourceDir().resolve("../../resources");
        Path target = Path.of("target/classes")
            .resolve(context.config().projectName())
            .resolve(context.module().name().value());

        if (!Files.exists(source)) return;

        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(p -> {
                try {
                    Path dest = target.resolve(source.relativize(p));
                    if (Files.isDirectory(p)) Files.createDirectories(dest);
                    else Files.copy(p, dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Resource copy failed", e);
                }
            });
        } catch (IOException e) {
            Logger.info("ℹ️ No resources found at " + source);
        }
    }
}
