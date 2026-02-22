package stitch.engine;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public record BuildGraph(Map<String, CompletableFuture<Void>> tasks) {
  public BuildGraph() {
    this(new ConcurrentHashMap<>());
  }

  public void register(String projectName) {
    tasks.put(projectName, new CompletableFuture<>());
  }

  public Optional<CompletableFuture<Void>> getDependencyTask(String projectName) {
    return Optional.ofNullable(tasks.get(projectName));
  }

  public void markComplete(String projectName) {
    getDependencyTask(projectName).ifPresent(t -> t.complete(null));
  }

  public void markFailed(String projectName, Throwable err) {
    getDependencyTask(projectName).ifPresent(t -> t.completeExceptionally(err));
  }
}

