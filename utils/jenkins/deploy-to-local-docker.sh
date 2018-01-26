#!/bin/bash

# IMPORTANT
# This script is meant to be executed after the Build process has finished.
# It builds a minimal docker image to deploy the application on the local docker daemon
# (application will be running on port 9001)

# Make the Working Directory
echo "Creating Working Directory"
mkdir server/target/docker_deploy

# Copying Dockerfile to working Directory
echo "Copying Dockerfile"
cp utils/jenkins/Dockerfile server/target/docker_deploy
cp docker/alpine-dind/toscana-dind-entrypoint.sh server/target/docker_deploy
cp docker/alpine-dind/install-deps.sh server/target/docker_deploy
cp docker/alpine-dind/cleanup.sh server/target/docker_deploy
# Copying server.jar in working Directory
echo "Copying server.jar"
cp server/target/server-1.0-SNAPSHOT.jar server/target/docker_deploy/server.jar

echo "Navigating into Working Directory"
cd server/target/docker_deploy

echo "Stopping old container (if running)"
docker stop toscana || true
echo "Deleting Container"
docker rm toscana || true
echo "Deleting Docker image (if present)"
docker rmi toscana/toscana:alpine-build || true

echo "Building Docker image"
docker build . -t toscana/toscana:alpine-build

echo "Running Docker image"
docker run -d -p 127.0.0.1:9001:8080 --privileged \
  -v toscana_data:/toscana/data --restart=unless-stopped \
  --name=toscana toscana/toscana:alpine-build

cd ..

echo "Removing Working Directory"
rm -r docker_deploy
