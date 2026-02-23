package stitch;

import stitch.util.Logger;

import java.io.IOException;
import java.nio.file.*;

public class Main {
  private static final String VERSION = "0.0.1";

  public static void main(String[] args) {
    if (args.length == 0) {
      printHelp();
      Logger.flush();
      return;
    }

    String input = args[0];
    if (input.endsWith(".java")) {
      runBuildScript(input);
      return;
    }

    switch (input.toLowerCase()) {
      case "init" -> initProject();
      case "version", "-v" -> Logger.info("Stitch Build System v" + VERSION);
      default -> printHelp();
    }
    Logger.flush();
  }

  private static void runBuildScript(String scriptPath) {
    try {
      Path path = Paths.get(scriptPath);
      if (!Files.exists(path)) {
        Logger.error("❌ Build script not found: " + scriptPath);
        Logger.flush();
        return;
      }

      // Find the literal physical location of the JAR file currently running this class
      String jarPath = Main.class.getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .toURI()
        .getPath();

      ProcessBuilder pb = new ProcessBuilder(
        "java",
        "--enable-preview",
        "--source", "25",
        "-cp", jarPath, // Use the absolute path to THIS jar
        scriptPath
      ).inheritIO();

      int result = pb.start().waitFor();
      Logger.flush();
      if (result != 0) {
        System.exit(result);
      }
    } catch (Exception e) {
      Logger.error("❌ Error executing build script: " + e.getMessage(), e);
      Logger.flush();
    }
  }

  private static void printHelp() {
    Logger.info("🧵 Stitch Build System - v" + VERSION);
    Logger.info("Usage: ./stitchw.java [script.java | command]");
    Logger.info("Commands: init, version");
  }

  private static void initProject() {
    try {
      Path root = Paths.get("").toAbsolutePath();
      Files.createDirectories(root.resolve("src/main/java/com/example"));
      Files.createDirectories(root.resolve("src/main/resources"));

      Files.writeString(root.resolve("src/main/java/module-info.java"),
        "module com.example {\n}\n");

      Files.writeString(root.resolve("stitch.java"), """
        import stitch.Workspace;
        
        void main() {
            Workspace.init("my-app")
                .project("app")
                .mainClass("com.example.Main")
                .execute();
        }
        """);

      // If you ever generate stitchw.java in the init step, make it executable
      Path wrapper = root.resolve("stitchw.java");
      if (Files.exists(wrapper)) {
        wrapper.toFile().setExecutable(true);
      }

      Logger.info("✅ Project initialized with Maven layout.");
    } catch (IOException e) {
      Logger.error("❌ Failed to initialize project.", e);
    }
  }
}