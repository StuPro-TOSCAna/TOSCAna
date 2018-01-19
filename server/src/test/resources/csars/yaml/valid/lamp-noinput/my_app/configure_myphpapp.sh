#!/bin/bash
CREDENTIALS="/var/www/html/mysql-credentials.php"

sed -i "s:DATABASE_HOST:${database_host}:g" $CREDENTIALS
sed -i "s:DATABASE_PASSWORD:${database_password}:g" $CREDENTIALS
sed -i "s:DATABASE_NAME:${database_name}:g" $CREDENTIALS
sed -i "s:DATABASE_PORT:${database_port}:g" $CREDENTIALS
sed -i "s:DATABASE_USER:${database_user}:g" $CREDENTIALS
