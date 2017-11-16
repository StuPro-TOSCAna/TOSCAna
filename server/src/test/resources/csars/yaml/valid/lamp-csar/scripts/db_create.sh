#!/bin/bash
# Setup MySQL root password and create user
cat << EOF | mysql -u root --password=abc
CREATE DATABASE mydb;
USE mydb;
create table tasks (id INT not null auto_increment,task varchar(255), primary key(id));
EXIT
EOF
