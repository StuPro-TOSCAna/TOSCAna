#!/bin/bash
# install php on a linux machine with php-mysql
sudo apt-get update -y && sudo apt-get upgrade -y
sudo apt-get install -y php php-mysql libapache2-mod-php7.0

sudo mv myphpapp.php /var/www/html/
sudo mv mysql-credentials.php /var/www/html/
