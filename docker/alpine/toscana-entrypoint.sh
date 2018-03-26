#!/bin/bash

# Launch Toscana
logfile=/var/log/toscana.log
touch $logfile
java -jar server.jar | tee -a $logfile
