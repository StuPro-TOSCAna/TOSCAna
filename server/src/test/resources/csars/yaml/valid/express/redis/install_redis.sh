#!/bin/bash

sudo apt-get upgrade
sudo apt-get install -y gcc make wget

wget http://download.redis.io/releases/redis-4.0.7.tar.gz
tar xzf redis-4.0.7.tar.gz
cd redis-4.0.7

make
mv src/redis-server ../
cd .. 

rm -r redis-4*
sudo apt-get autoremove -y gcc make wget
