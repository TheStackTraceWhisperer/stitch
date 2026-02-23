# Stitch Build System

A **JPMS-native** build system for Java 25+. Stitch leverages the Module System to provide deterministic builds without XML.

## 🚀 Features

- **Dependency Stitching**: Declare external Maven dependencies directly in your build script. Stitch resolves specific JARs and manages the module graph.
- **Automated Launch**: Automatically generates an executable launcher script (`./target/app`) that handles the `module-path` setup.
- **Self-Hosting**: Stitch builds itself.
- **Configuration as Code**: Build scripts are valid, runnable Java programs (`stitch.java`).

## 🛠️ Bootstrapping Stitch

Stitch builds itself. To get started from source:

```bash
# 1. Provide the initial build using Maven
# 2. Use the result to rebuild Stitch using Stitch (Self-Hosting)
./bootstrap-build.sh
```

This will produce the finalized build engine in `target/.stitch/stitch.jar`.

## 📖 Getting Started

### 1. Project Structure
Stitch follows standard Maven conventions but uses a simpler configuration:

```
my-project/
├── src/main/java/
│   ├── module-info.java
│   └── com/example/app/Main.java
├── stitch.java      <-- The Build Script
└── stitchw.java     <-- The Wrapper
```

### 2. The Build Script (`stitch.java`)
Configure your workspace using the Java DSL:

```java
import stitch.Workspace;

void main() {
    Workspace.init("my-workspace")
        // Stitch external dependencies from Maven Central
        .stitch("com.google.gson", "com.google.code.gson:gson:2.10.1")
        
        // Define your project
        .project("app")
            .moduleName("com.example.app")
            .mainClass("com.example.app.Main")
            .execute();
}
```

### 3. Running the Build
Use the wrapper script to build your project. It automatically downloads the Stitch bootstrapper if needed.

```bash
./stitchw.java
```

**Output:**
```
🚀 Starting Stitch build engine with 1 projects...
🧵 [app] Starting task...
✅ Found cached dependency: gson-2.10.1.jar
⏳ [app] Awaiting dependencies: [com.google.gson]
🔨 [app] Compiling...
📦 [app] Created JAR: target/app.jar
📚 [app] Copied dependencies to: target/lib
🚀 Run your app effortlessly with: ./target/app
✅ [app] Finished.
🏁 Build process completed.
```

### 4. Running Your App
Stitch generates a launcher script to run modular applications:

```bash
./target/app
```

This avoids manual `java -p ... -m ...` commands.

## 🧩 Dependency Management

Stitch doesn't use `pom.xml`. Instead, you "stitch" external artifacts into your workspace map.

1.  **Declare** the mapping in `stitch.java`:
    ```java
    .stitch("com.google.gson", "com.google.code.gson:gson:2.10.1")
    ```
2.  **Require** it in `module-info.java`:
    ```java
    module com.example.app {
        requires com.google.gson; 
    }
    ```

Stitch will automatically:
1.  Check strictly named modules.
2.  Download the JAR from Maven Central.
3.  Cache it in `.stitch/cache`.
4.  Add it to the compiler's module path.
5.  Bundle it into `target/lib` for runtime execution.

## 📋 Requirements

- **JDK 25** (Preview features enabled)
- Linux/macOS (Windows support planned)

## 🏗️ Architecture

Stitch is built on a **Concurrent Pipeline** model. Each project is a task submitted to a virtual thread pool.

**Build Pipeline:**
1.  **ParseModuleStep**: Reads `module-info.java`.
2.  **FetchDependenciesStep**: Resolves "stitched" JARs from Maven Central.
3.  **AwaitDependenciesStep**: Blocking wait for inter-project dependencies in the graph.
4.  **CompileStep**: Invokes `javax.tools.JavaCompiler`.
5.  **CopyResourcesStep**: Copies non-Java assets.
6.  **PackageJarStep**: Creates the JAR and the Launch Script.

---
*Stitch is currently in active development / alpha.*
## License
MIT License
---
**Version:** 0.0.1  
**Built with:** Java 25 + JPMS Modules
