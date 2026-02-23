#!/bin/sh

echo "> Running initial Maven build"
mvn clean verify
# If the above succeeds, we have the initial bootstrap jar (stitch-0.0.1.jar) in target/

mkdir -p target/.stitch
cp ./target/stitch-0.0.1.jar ./target/.stitch/stitch.jar
# The target/.stitch directory is now seeded with the Maven-built JAR for the wrapper to use

echo "> Cleaning target directory (preserving bootstrap jar)"
find ./target/* -maxdepth 1 ! -name '.stitch' ! -name '.' -exec rm -rf {} +
# The target folder is now empty except for the bootstrap .stitch/stitch.jar

echo "> Phase 1: Using Maven-built Stitch to build Stitch"
./stitchw.java
# If successful, we now have a self-built 'target/stitch-build-system.jar'

echo "> Archiving Maven build to stitch-maven.jar"
mv ./target/.stitch/stitch.jar ./target/.stitch/stitch-maven.jar
# We move the Maven build aside to prove we aren't using it anymore

echo "> Phase 2: Seeding the self-built jar as the new bootstrap"
cp ./target/stitch-build-system.jar ./target/.stitch/stitch.jar
# The wrapper will now use the self-built JAR for the next execution

echo "> Phase 3: Using Stitch-built Stitch to build Stitch (Self-Hosting)"
./stitchw.java
# If this succeeds, Stitch has successfully built itself using itself!
