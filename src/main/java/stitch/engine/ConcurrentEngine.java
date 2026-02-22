package stitch.engine;

import stitch.domain.ProjectConfig;
import stitch.util.Logger;

import java.util.Map;
import java.util.concurrent.Executors;

public class ConcurrentEngine {
    public static void execute(Map<String, ProjectConfig> projects) {
        BuildGraph graph = new BuildGraph();
        projects.keySet().forEach(graph::register);

        Logger.info("🚀 Starting Stitch build engine with " + projects.size() + " projects...");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            projects.values().forEach(config ->
                executor.submit(new ProjectTask(config, graph))
            );
        }
        Logger.info("🏁 Build process completed.");
    }
}
