package stitch.engine.step;

import stitch.domain.ProjectContext;
import stitch.domain.ModuleName;
import stitch.resolve.DependencyResolver;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchDependenciesStep implements BuildStep {
    @Override
    public void execute(ProjectContext context) {
        Map<String, String> stitched = context.config().stitchedModules();
        List<Path> resolvedJars = new ArrayList<>();

        for (ModuleName req : context.module().requires()) {
            String moduleName = req.value();
            if (stitched.containsKey(moduleName)) {
                String coordinates = stitched.get(moduleName);
                Path jar = DependencyResolver.resolve(coordinates);
                resolvedJars.add(jar);
            }
        }

        context.setResolvedDependencies(resolvedJars);
    }
}

