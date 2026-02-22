#!/bin/sh

mvn clean verify && \
  mkdir -p target/.stitch && \
  cp ./target/stitch-0.0.1.jar ./target/.stitch/stitch.jar && \
  find ./target/* -maxdepth 1 ! -name '.stitch' ! -name '.' -exec rm -rf {} + && \
  ./stitchw.java && \
  mv ./target/.stitch/stitch.jar ./target/.stitch/stitch-maven.jar && \
  cp ./target/stitch-build-system.jar ./target/.stitch/stitch.jar && \
  ./stitchw.java