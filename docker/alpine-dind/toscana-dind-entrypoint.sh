#!/bin/bash

# Launch Dockerd
docker_logfile=/var/log/dockerd.log
touch $docker_logfile
while true; do dockerd | tee -a $docker_logfile; done &

# Launch Toscana
logfile=/var/log/toscana.log
touch $logfile
java -jar server.jar | tee -a $logfile

