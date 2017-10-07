FROM linode/lamp
MAINTAINER hnicke
COPY index.php /var/www/example.com/public_html/
COPY mysql-credentials.php /var/www/example.com/public_html/
COPY createdb.sql /
EXPOSE 80

RUN service mysql start && \
    rm /var/www/example.com/public_html/index.html && \
    mysql -u root --password="Admin2015" < /createdb.sql && \
    service mysql stop && \
    apt-get update 
ENTRYPOINT service apache2 restart && \
    service mysql restart && \
    /bin/bash


