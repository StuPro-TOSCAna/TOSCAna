#!/bin/sh
current_directory=$(pwd)
./kubectl config set-cluster minikube --server=https://localhost:8443 --certificate-authority=${current_directory}/ca.crt 
./kubectl config set-context minikube --cluster=minikube --user=minikube 
./kubectl config set-credentials minikube --client-key=${current_directory}/client.key --client-certificate=${current_directory}/client.crt


docker exec -it test cat /root/.minikube/ca.crt > ca.crt
docker exec -it test cat /root/.minikube/client.key > client.key
docker exec -it test cat /root/.minikube/client.crt > client.crt
