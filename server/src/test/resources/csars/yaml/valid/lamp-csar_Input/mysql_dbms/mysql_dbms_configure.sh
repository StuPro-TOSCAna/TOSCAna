#!/bin/bash
cat << EOF | mysql -u root --password=${database_password}
CREATE DATABASE mydb;
USE mydb;
create table tasks (id INT not null auto_increment,task varchar(255), primary key(id));
EXIT
EOF
