#!/bin/bash  
sudo apt-get update && sudo apt-get install -y docker-ce
sudo groupadd docker
sudo usermod -aG docker $USER
