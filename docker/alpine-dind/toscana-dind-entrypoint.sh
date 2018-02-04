#!/bin/bash
dockerd &
logfile=/var/log/toscana.log
touch $logfile
java -jar server.jar | tee -a $logfile

