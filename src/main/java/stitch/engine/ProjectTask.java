package stitch.engine;

import stitch.domain.*;
import stitch.engine.step.*;
import stitch.util.Logger;

import java.util.List;

public class ProjectTask implements Runnable {
    private final ProjectConfig config;
    private final BuildGraph graph;
    private final List<BuildStep> pipeline;

    public ProjectTask(ProjectConfig config, BuildGraph graph) {
        this.config = config;
        this.graph = graph;
        this.pipeline = List.of(
            new ParseModuleStep(),
            new AwaitDependenciesStep(graph),
            new CompileStep(),
            new CopyResourcesStep(),
            new PackageJarStep()
        );
    }

    @Override
    public void run() {
        try {
            Logger.info("🧵 [" + config.projectName() + "] Starting task...");
            ProjectContext context = new ProjectContext(config);

            for (BuildStep step : pipeline) {
                step.execute(context);
            }

            Logger.info("✅ [" + config.projectName() + "] Finished.");
            graph.markComplete(config.projectName());
        } catch (Exception e) {
            Logger.error("❌ [" + config.projectName() + "] Failed: " + e.getMessage(), e);
            graph.markFailed(config.projectName(), e);
            throw new RuntimeException(e);
        }
    }
}
