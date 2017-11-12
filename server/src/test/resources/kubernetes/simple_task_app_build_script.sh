#!/bin/sh

echo "Check if docker is available."                                                      
if ! [ -x "$(command -v docker)" ]; then                                                 
  echo 'Error: docker is not installed.' >&2                                             
  exit 1                                                                                 
fi                                                                                       
                                                                                            
if ! [ -x "$(pgrep -f docker > /dev/null)" ]; then                                        
  echo 'Error: docker daemon is not running.' >&2                                      
  exit 1                                                                               
fi

docker image -t simple-task-app ../simple-task-app/

if [ $? -eq 0 ]; then
 echo "Building the docker image was successful!"                                    
else
 echo "Building the docker image failed! Please check the console output."           
fi
