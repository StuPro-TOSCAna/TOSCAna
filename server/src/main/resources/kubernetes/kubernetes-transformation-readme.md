# TOSCAna - Kubernetes Transformation Artifact

Congratulations! You have transformed a TOSCA topology using the Kubernetes Plugin of the TOSCAna Transformer. This readme will
guide you through the deployment process for the transformed application.

## Requirements

The following section describes the requirements for your environment in order to deploy this transformation artifact.

### Deployment Machine Requirements

- Kubectl (Version 1.8 or newer)
  - The Kubectl context for the cluster you want to deploy to has to be set as default
  - The set context/configuration needs the privileges to deploy on the cluster
- Bash (Tested with Version 4.4.19)
- *OPTIONAL*: Jq (Tested with Version 1.5)
  - jq is used to parse the Ports of the resulting services after the deployment is done. If jq is missing this step will be skipped.

Supported Platforms:
- Linux
- MacOS (Only with already Pushed Images, Won't work properly because of the different `sed` implementation)
- Windows (Untested, Might work for already Pushed Images with MSYS or Cygwin)

#### Additional Requirements for Pushing to a Registry

- Docker (including Daemon with Version 17.03 or Later)
  - The User executing the script has to be in the `docker` group
  - Only Linux-based Docker is supported
- A Docker Registry with Write access
  - If Reading from the Registry requires Credentials automated deployment using the `deploy.sh` script is not possible, because the Credentials have to be manually set in the cluster. (See **Further Reading** for more information)

### Cluster Requirements

- Kubernetes Version 1.8 or newer
- KubeDNS (on the cluster)
- Linux based Kubernetes Cluster

## Deploy Guide

### Before you deploy

This section gives a quick overview about the commands that have to be executed before running a deployment.

#### Login to the Docker Registry

The Push utility requires you to be logged into the registry you want to push to using the `docker login` command like this:

***For DockerHub***:
```bash
docker login
```

***For another registry***:
```bash
docker login <REGISTRY_URL>
```

This command will ask you for your username and password. The command will succeed if the login succeeded. Otherwise the command will fail.

#### Make the shell scripts executable

To make the shell script executable, use a terminal to navigate to the extracted `output` directory and run the following command:
```bash
chmod +x deploy.sh
```

**NOTE** It is not required to make the `push-images.sh` executable because the deploy script will do that for you before starting the script.

### Using the automated deploy scripts

To run the automated deployment just run
```bash
./deploy.sh
```
in the `output` directory and follow the on screen instructions.

### Deploying manually

In some cases the Automated deployscript cannot be used. For example because you need to specify the registry secret in the kubernetes manifest.

In that case you can still use the `push-images.sh` script to push the images to the registry and to modify the manifest to feature the resulting image tags (including the registry URL).-

Assuming the images have been pushed you can use
```bash
kubectl create -f kubernetes-resources/complete.yml
```
to deploy the result to the cluster manually

## Further Reading

### TOSCAna
- [TOSCAna on GitHub](https://github.com/StuPro-TOSCAna/TOSCAna)
- [TOSCAna Documentation on ReadTheDocs](https://toscana.readthedocs.io/en/latest/)

### Jq
- [Jq Website](https://stedolan.github.io/jq/)

### Docker
- [Docker Login - Documentation](https://docs.docker.com/engine/reference/commandline/login/)

### Kubectl
- [Kubectl Overview](https://kubernetes.io/docs/reference/kubectl/overview/)

### Kubernetes
- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [Kubernetes Service Types](https://kubernetes.io/docs/concepts/services-networking/service/)
- [Kubernetes - Pulling from private Repositories](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/)

