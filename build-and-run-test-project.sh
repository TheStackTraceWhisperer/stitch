#!/bin/bash

if [ ! -f "target/.stitch/stitch.jar" ]; then
    echo "Stitch JAR not found. Bootstrapping..."
    ./bootstrap-build.sh
fi

echo "Copying Stitch JAR and wrapper to test project..."
mkdir -p test-project/target/.stitch
cp target/.stitch/stitch.jar test-project/target/.stitch/stitch.jar

echo "Running test project..."
cd test-project
./stitchw.java
echo "Launching test application..."
./target/app
