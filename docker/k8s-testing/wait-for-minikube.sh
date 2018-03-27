#!/bin/bash

for i in `seq 1 300`; do
    echo "Waiting for Minikube $i/300"
    if kubectl get nodes; then
        exit 0;
    fi
    sleep 1
done
