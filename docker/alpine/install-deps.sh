#!/bin/bash
echo "Installing APK Packages"
apk add --update --no-cache curl openjdk8-jre python3

echo "Installing AWS CLI"
pip3 install awscli

echo "Installing CloudFoundry CLI"
curl -L "https://packages.cloudfoundry.org/stable?release=linux64-binary&source=github" | tar -zx
mv cf /usr/local/bin
