#!/bin/bash
# TOSCAna Kubernetes - Push Script

if ! [ -x "$(command -v "docker")" ]; then
  echo "Error: docker is not installed." >&2
  exit 1
fi

echo "TOSCAna - Kubernetes - Registry Push Utility"
echo "############################################"
echo ""
echo "This script assumes, that the Docker CLI is setup to work properly"
echo "and the User used to push to the registry is logged in to the registry"
echo "using the 'docker login <REGISTRY_URL>' command."
echo ""

printf "Please enter the Registry URL (Without leading http:// or https://, Empty for DockerHub): "
read reg_url

[ -n "$reg_url" ] && [[ "$reg_url" =~ [^/]*$ ]] && reg_url="$reg_url/"

printf "Please enter the Username for the given registry: "
read reg_username
if [ -z "$reg_username" ]; then
  echo "You have to Specify a username!"
  exit 1
fi

printf "Please enter the repository name: "
read reg_repo_name
if [ -z "$reg_repo_name" ]; then
  echo "You have to Specify a repository!"
  exit 1
fi

echo "Creating Backup of Kubernetes Resource"
cp kubernetes-resources/complete.yml kubernetes-resources/complete-original.yml

cd docker
for file in *.tar.gz
do
  echo "Loading image archive: $file"

  #Import the image with pv (or cat if pv isnt installed)
  if ! [ -x "$(command -v "pv")" ]; then
    docker load < $file
  else
    pv $file | docker load
  fi

  name=$(echo $file | sed -e "s:.tar.gz::g")
  img_name=$name:latest
  new_tag=$reg_url$reg_username/$reg_repo_name:$name

  echo "Tagging $img_name as $new_tag"
  docker tag $img_name $new_tag

  echo "Pushing $new_tag"
  docker push $new_tag

  echo "Removing tag with $img_name"
  docker rmi $img_name

  echo "Replacing Tags in Kubernetes Resource"
  sed -i "s|image: \"${name}\"|image\: \"${new_tag}\"|g" ../kubernetes-resources/complete.yml
done
