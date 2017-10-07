FROM janes/alpine-lamp
MAINTAINER hnicke
COPY index.php /www
COPY mysql-credentials.php /www
COPY createdb.sql /
EXPOSE 80
EXPOSE 3306

RUN nohup mysqld --skip-grant-tables --bind-address 0.0.0.0 --user mysql > /dev/null 2>&1 & \
    sleep 3 && mysql -u root < /createdb.sql
ENTRYPOINT httpd && \
           nohup mysqld --skip-grant-tables --bind-address 0.0.0.0 --user mysql > /dev/null 2>&1 & \
           /bin/sh

