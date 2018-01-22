#!/bin/bash
echo "Cleanup"
apk del maven curl git nodejs
rm -rf /root/.m2/ install-deps.sh build-toscana.sh cleanup.sh
