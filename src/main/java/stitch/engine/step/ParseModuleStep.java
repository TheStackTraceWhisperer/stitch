package stitch.engine.step;

import stitch.domain.*;
import stitch.resolve.ModuleInfoParser;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParseModuleStep implements BuildStep {
    @Override
    public void execute(ProjectContext context) {
        ProjectConfig config = context.config();
        Path moduleInfoPath = config.sourceDir().resolve("module-info.java");

        if (!Files.exists(moduleInfoPath)) {
            throw new ModuleResolutionException("Missing module-info.java at " + moduleInfoPath.toAbsolutePath());
        }

        ParsedModule parsed = ModuleInfoParser.parse(moduleInfoPath);
        ModuleName effectiveName = config.moduleNameOverride()
            .map(ModuleName::new)
            .orElse(parsed.name());

        context.setModule(new ParsedModule(effectiveName, parsed.requires()));
    }
}

