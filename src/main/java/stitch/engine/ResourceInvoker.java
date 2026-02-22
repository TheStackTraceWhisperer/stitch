package stitch.engine;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class ResourceInvoker {
  public static void copy(Path source, Path target) {
    if (!Files.exists(source)) return;
    try (Stream<Path> stream = Files.walk(source)) {
      stream.forEach(p -> {
        try {
          Path dest = target.resolve(source.relativize(p));
          if (Files.isDirectory(p)) {
            Files.createDirectories(dest);
          } else {
            Files.copy(p, dest, StandardCopyOption.REPLACE_EXISTING);
          }
        } catch (IOException e) {
          throw new RuntimeException("Resource copy failed", e);
        }
      });
    } catch (IOException e) {
      System.out.println("ℹ️ No resources found at " + source);
    }
  }
}