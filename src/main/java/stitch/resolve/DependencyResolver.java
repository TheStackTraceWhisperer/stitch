package stitch.resolve;

import stitch.domain.ModuleResolutionException;
import stitch.util.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class DependencyResolver {
    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";
    private static final Path CACHE_DIR = Path.of(".stitch", "cache");
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public static Path resolve(String coordinates) {
        String[] parts = coordinates.split(":");
        if (parts.length != 3) {
            throw new ModuleResolutionException("Invalid coordinates: " + coordinates + ". Expected groupId:artifactId:version");
        }

        String groupId = parts[0].replace('.', '/');
        String artifactId = parts[1];
        String version = parts[2];

        String fileName = artifactId + "-" + version + ".jar";
        Path cachedFile = CACHE_DIR.resolve(fileName);

        if (Files.exists(cachedFile)) {
            Logger.info("✅ Found cached dependency: " + fileName);
            return cachedFile.toAbsolutePath(); // Always return absolute paths to the compiler
        }

        String url = MAVEN_CENTRAL + groupId + "/" + artifactId + "/" + version + "/" + fileName;
        return download(url, cachedFile);
    }

    private static Path download(String url, Path destination) {
        try {
            Files.createDirectories(destination.getParent());
            Logger.info("🔽 Fetching stitched dependency: " + destination.getFileName());

            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<Path> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofFile(destination));

            if (response.statusCode() != 200) {
                Files.deleteIfExists(destination);
                throw new ModuleResolutionException("HTTP " + response.statusCode() + " downloading " + url);
            }

            Logger.info("✅ Downloaded: " + destination.getFileName());
            return destination.toAbsolutePath(); // Always return absolute paths
        } catch (IOException | InterruptedException e) {
            throw new ModuleResolutionException("Failed to download " + url, e);
        }
    }
}

