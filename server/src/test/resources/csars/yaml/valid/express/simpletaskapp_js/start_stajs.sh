#!/bin/bash

export REDIS_URL="redis://$REDIS_HOST:$REDIS_PORT/$REDIS_DB"

npm start
