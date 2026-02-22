package stitch.engine.step;

import stitch.domain.ProjectContext;

public interface BuildStep {
    void execute(ProjectContext context);
}

