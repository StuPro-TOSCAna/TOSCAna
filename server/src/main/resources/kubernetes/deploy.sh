#!/bin/bash
echo "Deploy TOSCA model to Kubernetes"
echo "################################"
echo ""
echo "This script assumes the 'Kubectl' context to be set properly."
echo "Please consult the Kubernetes documentation to find out how to do this."
echo "Kubectl Overview: https://kubernetes.io/docs/reference/kubectl/overview/"
echo ""
echo "If the images have not been pushed before please ensure you have 'docker' (including the daemon) installed"
echo "and you used the 'docker login <REGISTRY_URL>' command to login to the registry you want to push to."
echo ""
echo "IMPORTANT NOTE:"
echo "We currently only support the Automated Deployment for repositories with public read access."
echo "To deploy from a private repository you have to manually add the 'imagePullSecret' to each created Deployment"
echo "A Secret, that sets the credentials in the cluster also has to be supplied!"
echo "For further information please visit: https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/"
echo ""
printf "Do you want to continue (y/N): "
read input

if [ "$input" != "y" ] && [ "$input" != "yes" ] && [ "$input" != "Y" ]
then
  echo "Aborted!"
  exit 1
fi

#Check if Kubectl is installed
if ! [ -x "$(command -v "kubectl")" ]; then
  echo "Error: kubectl is not installed." >&2
  exit 1
fi

echo "Checking Cluster Status..."
kubectl get nodes --request-timeout='10s' --stderrthreshold=100 >> /dev/null
if  [ $? -ne 0 ]
then
  echo "'kubectl get nodes' returned a non 0 error code. Please check your Cluster Status and Context Configuration" >&2
  exit 1
fi

# Execute the push images script if the Script exists.
# The Kubernetes Plugin only copies this file if pushing needs to happen.
if [ -f push-images.sh ]
then
  echo "Checking for Docker"
  # Check if docker is installed
  # This is only needed if you want to push to a registry
  if ! [ -x "$(command -v "docker")" ]; then
    echo "Error: docker is not installed." >&2
    exit 1
  fi

  echo "Pushing Images"
  chmod +x push-images.sh
  ./push-images.sh
fi

echo "Creating resources"
kubectl create -f kubernetes-resources/complete.yml

echo "Services Overview:"
kubectl get services
echo "Deployments"
kubectl get deployments

if ! [ -x "$(command -v "jq")" ]
then
  echo "Cannot print further informaiton because 'jq'  is not installed"
  echo "Done"
  exit 1
fi

echo "Your services can be accessed under the following Addresses"
echo ""
printf "%-30s %-15s %-15s %-15s %-25s\n" "Service Name" "Source Port" "Address" "Target Port" "URL"
for service_name in $(kubectl get services -o json | jq -r '.items[] | select(.spec.type == "NodePort") | .metadata.name')
do
  ports=$(kubectl get service $service_name -o json | jq -r '.spec.ports[] | {"source": .port, "target": .nodePort}' )
  for source_port in $(echo $ports | jq -r '.source')
  do
    target_port=$(echo $ports | jq -r "select(.source == $source_port) | .target")
    for address in $(kubectl get nodes --output json | jq -r '.items | sort_by(.spec.nodeName)[] | .status.addresses[] | select(.type == "InternalIP") | .address')
    do
      printf "%-30s %-15s %-15s %-15s %-25s\n" "$service_name" "$source_port" "$address" "$target_port" "$address:$target_port"
    done
  done
done
echo ""
echo "Done!"
