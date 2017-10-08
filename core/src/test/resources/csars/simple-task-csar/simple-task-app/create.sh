#!/bin/bash  
# IMPORTANT:    don't simply run script like this:  ./create
#               instead, run it like this:          . create
# This sources the script (executes commands in same shell);
# the exported endpoint will therefore be available afterwards
docker build -t simple-task-app .
docker create -p 80:80 --name="simple-task-app-1" -i simple-task-app &&
export generated_endpoint=$(curl -s http://whatismyip.akamai.com/)
echo "created simple-task-app. endpoint is: '$ip'"
