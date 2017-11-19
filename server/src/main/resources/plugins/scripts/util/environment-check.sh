#!/usr/sh

#!/bin/sh

function check () {
  echo "Check if $1 is available."                                                      
  if ! [ -x "$(command -v $1)" ]; then                                                 
    echo "Error: $1 is not installed." >&2                                             
    exit 1                                                                                 
  fi
}
