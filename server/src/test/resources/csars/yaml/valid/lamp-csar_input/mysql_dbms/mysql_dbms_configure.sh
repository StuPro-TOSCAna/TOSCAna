#!/bin/bash
cat << EOF | mysql -u root --password=${database_password}
CREATE DATABASE ${database_name};
USE ${database_name};
create table tasks (id INT not null auto_increment,task varchar(255), primary key(id));
EXIT
EOF
