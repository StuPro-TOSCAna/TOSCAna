#!/bin/bash

target=$1
cd ../cli/
mvn package -DskipTests=true
cp target/cli-1.0-SNAPSHOT-jar-with-dependencies.jar $1
cd ..
