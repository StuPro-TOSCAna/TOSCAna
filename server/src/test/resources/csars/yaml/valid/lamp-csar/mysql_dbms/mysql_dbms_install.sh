#!/bin/bash
# install mysql-server without password promt.
# insert a fix pw
sudo apt-get update -y && sudo apt-get upgrade -y
echo "mysql-server mysql-server/root_password password abc" | sudo debconf-set-selections
echo "mysql-server mysql-server/root_password_again password abc" | sudo debconf-set-selections
sudo apt-get install mysql-server -y
sudo systemctl enable mysql
