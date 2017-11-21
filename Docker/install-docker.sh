#!/bin/sh

apt-get -y install \
    apt-transport-https \
    ca-certificates \
    software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -

add-apt-repository -y \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"

apt-get update

# this fails everytime because systemd is not available on install
apt-get install -y docker-ce || true
