#!/bin/bash
$(echo "USE $MYSQL_DATABASE;create table tasks (id INT not null auto_increment,task varchar(255), primary key(id));" | mysql -u root --password=$MYSQL_ROOT_PASSWORD)
