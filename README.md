# Stitch Build System
A modern Java build system leveraging JPMS modules, domain-driven design, and incremental compilation.

## Bootstrapping Stitch

```shell
mvn clean verify && \
  mkdir -p target/.stitch && \
  cp ./target/stitch-0.0.1.jar ./target/.stitch/stitch.jar && \
  find ./target/* -maxdepth 1 ! -name '.stitch' ! -name '.' -exec rm -rf {} + && \
  ./stitchw.java && \
  mv ./target/.stitch/stitch.jar ./target/.stitch/stitch-maven.jar && \
  cp ./target/stitch-build-system.jar ./target/.stitch/stitch.jar && \
  ./stitchw.java &&
```


## Features
✨ **JPMS Module System** - Native module support with `module-info.java`  
🚀 **Module-Aware Resolution** - Intelligent dependency graph traversal  
🔄 **BOM Support** - Import Spring Boot, Quarkus, and other BOMs  
📦 **Incremental Compilation** - Only recompile changed files  
🎯 **Clean Architecture** - Immutable domain models and functional design  
⚡ **Fast Builds** - Staleness detection and parallel resolution
## Quick Start
### Installation
```bash
java -jar stitch-0.0.1.jar init
```
This creates:
- `src/module-info.java` - Module descriptor
- `build.java` - Build configuration
- `stitch.java` - Bootstrap wrapper
### Build Configuration
Create a `build.java` file:
```java
import stitch.Workspace;
void main() {
    Workspace.init("my-app")
        .mapModule("gson", "com.google.code.gson:gson:2.10.1")
        .project("app")
            .moduleName("com.example.myapp")
            .mainClass("com.example.Main")
            .requiresModule("gson")
            .execute();
}
```
### Run the Build
```bash
# Using JEP 330 source-file execution
java --source 25 --enable-preview build.java
# Or compile first
javac --enable-preview --source 25 build.java
java --enable-preview build
```
### Run Your Application
```bash
java -p target/modules:target/my-app.jar -m com.example.myapp
```

## Examples

See the `examples/` directory for complete working projects:

- **hello-gson** - Simple JSON serialization with Gson
- **simple-text** - Text processing with Apache Commons (demonstrates transitive dependencies)

Each example is self-contained with:
- `stitchw.java` - Bootstrap wrapper (auto-downloads Stitch)
- `stitch.java` - Build script
- `src/main/java/` - Standard Maven layout

```bash
cd examples/hello-gson
./stitch.java
java --enable-preview -p target/modules:target/hello-gson.jar -m com.example.hello
```

## Requirements

- **JDK 25+** with preview features enabled
- **Maven 3.8+** (for building Stitch itself)

## Cache Location

Dependencies are cached per-project in `.stitch/cache/`

Benefits:
- **Project isolation** - Each project has its own dependency cache
- **Reproducible builds** - No global state pollution
- **Easy cleanup** - Just delete `.stitch/` directory
- **Version control** - Add `.stitch/` to `.gitignore`

## Building Stitch
```bash
mvn clean package
```
Output: `target/stitch-0.0.1.jar`
## Documentation
See [ARCHITECTURE.md](ARCHITECTURE.md) for complete technical documentation.
## License
MIT License
---
**Version:** 0.0.1  
**Built with:** Java 25 + JPMS Modules
