# TOSCAna - Kubernetes Transformation Artifact

## Requirements

### Deployment Machine Requirements

- Kubectl (Version 1.8 or newer)
  - The Kubectl context for the cluster you want to deploy to has to be set as default
  - The set context/configuration needs the priviliges to deploy on the cluster
- Bash (Tested with Version 4.4.19)
- *OPTIONAL*: Jq (Tested with Version 1.5)
  - jq is used to parse the Ports of the resulting services after the deployment is done. If jq is missing this step will be skipped.

Supported Platforms:
- Linux
- MacOS (Only with already Pushed Images)
- Windows (Untested, Might work with MSYS or Cygwin)

#### Additional Requirements for Pushing to a Registry

- Docker (including Daemon with Version 17.03 or Later)
  - The User executing the script has to be in the `docker` group
  - Only Linux-based Docker is supported
- A Docker Registry with Write access
  - If Reading from the Registry requires Credentials automated deployment using the `deploy.sh` script is not possible, because the Credentials have to be manually set in the cluster. (See **Further Reading** for more information)

### Cluster Requirements

- Kubernetes Version 1.8 or newer
- KubeDNS (on the cluster)

## Deploy Guide

### Before you deploy

### Using the automated deploy scripts

### Deploying manually

## Customisations

## Further Reading

### TOSCAna
- [TOSCAna on GitHub](https://github.com/StuPro-TOSCAna/TOSCAna)
- [TOSCAna Documentation on ReadTheDocs](https://toscana.readthedocs.io/en/latest/)

### Kubernetes
- [Kubernets Documentation](https://kubernetes.io/docs/home/)
