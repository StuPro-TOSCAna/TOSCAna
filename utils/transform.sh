#!/bin/sh

# first argument: path to cli.jar
# second argument: path to source_csar
# third argument: target platform

alias toscana="java -jar $1"

source_csar_path=$2
source_csar=${source_csar_path##*/}
source_csar=${source_csar%.*}
target_platform=$3
toscana csar delete -c $source_csar
toscana transformation delete -c $source_csar -p $target_platform
toscana csar upload -f $source_csar_path
toscana transformation start -c $source_csar -p $target_platform
sleep 5
toscana transformation download -c $source_csar -p $target_platform
