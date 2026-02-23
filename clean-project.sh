#!/bin/sh

echo "Cleaning root project artifacts..."
rm -rf target/

echo "Cleaning test-project artifacts..."
rm -rf test-project/target/
rm -rf test-project/.stitch/

echo "Clean complete."
