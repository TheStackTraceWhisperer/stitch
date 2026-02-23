///usr/bin/env java --source 25 --enable-preview "$0" "$@"; exit $?

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

final String STITCH_VERSION = "0.0.1";
final String DEFAULT_MAVEN_REPO = "https://repo1.maven.org/maven2";
final String ARTIFACT_PATH = "/io/github/stitch/stitch/" + STITCH_VERSION + "/stitch-" + STITCH_VERSION + ".jar";

void main(String[] args) throws Exception {
    Path stitchJar = locateStitchJar();
    fetchStitchJarIfNotFound(stitchJar);
    executeStitch(stitchJar, args);
}

Path locateStitchJar() {
    // Stick to the target/.stitch location you defined in your script
    return Path.of("target", ".stitch", "stitch.jar").toAbsolutePath();
}

void fetchStitchJarIfNotFound(Path stitchJar) throws IOException, InterruptedException {
    if (Files.exists(stitchJar)) return;

    System.out.println("🔽 Downloading Stitch Build System...");
    Path home = stitchJar.getParent();
    if (home != null) Files.createDirectories(home);

    String repo = System.getProperty("STITCH_MAVEN_REPOSITORY", DEFAULT_MAVEN_REPO);
    URI downloadUrl = URI.create(repo + ARTIFACT_PATH);

    HttpClient client = HttpClient.newBuilder()
      .followRedirects(HttpClient.Redirect.NORMAL)
      .build();

    HttpRequest request = HttpRequest.newBuilder(downloadUrl).GET().build();
    HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(stitchJar));

    if (response.statusCode() != 200) {
        System.err.println("❌ Failed to download Stitch: HTTP " + response.statusCode());
        Files.deleteIfExists(stitchJar);
        System.exit(1);
    }
}

void executeStitch(Path stitchJar, String[] args) throws IOException, InterruptedException {
    String script = (args.length > 0 && args[0].endsWith(".java")) ? args[0] : "stitch.java";
    String jarPath = stitchJar.toAbsolutePath().toString();

    ProcessBuilder pb = new ProcessBuilder(
      "java",
      "--enable-preview",
      "-cp", jarPath,
      "stitch.Main",
      script
    ).inheritIO();

    System.exit(pb.start().waitFor());
}