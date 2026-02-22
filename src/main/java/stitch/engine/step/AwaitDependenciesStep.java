package stitch.engine.step;

import stitch.domain.ProjectContext;
import stitch.engine.BuildGraph;
import stitch.util.Logger;

import java.util.concurrent.CompletableFuture;

public class AwaitDependenciesStep implements BuildStep {
    private final BuildGraph graph;

    public AwaitDependenciesStep(BuildGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(ProjectContext context) {
        if (!context.module().requires().isEmpty()) {
            Logger.info("⏳ [" + context.config().projectName() + "] Awaiting dependencies: " + context.module().requires());
        }
        context.module().requires().forEach(req ->
            graph.getDependencyTask(req.value()).ifPresent(CompletableFuture::join)
        );
    }
}
