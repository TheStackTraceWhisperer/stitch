///usr/bin/env java --source 25 --enable-preview "$0" "$@"; exit $?

import stitch.Workspace;

void main() {
    Workspace.init("stitch")
      .project("stitch-build-system") // Defaults to src/main/java
      .moduleName("stitch")
      .mainClass("stitch.Main")
      .execute();
}

