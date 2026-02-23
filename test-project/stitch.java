import stitch.Workspace;

void main() {
    Workspace.init("my-app")
        .stitch("com.google.gson", "com.google.code.gson:gson:2.10.1")
        .project("app")
            .moduleName("com.example.app")
            .sourceDir("src/main/java")
            .mainClass("com.example.app.Main")
            .execute();
}

