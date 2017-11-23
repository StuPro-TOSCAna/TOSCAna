#!/bin/bash  
# IMPORTANT:    don't simply run script like this:  ./create
#               instead, run it like this:          . create
# This sources the script (executes commands in same shell);
# the exported endpoint will therefore be available afterwards
docker build -t $tag .
docker create -p 80:80 --name="$identifier" -i $tag &&
export endpoint=$(curl -s http://whatismyip.akamai.com/)
echo "created container '$identifier' from image "$tag". endpoint is: '$endpoint'"
