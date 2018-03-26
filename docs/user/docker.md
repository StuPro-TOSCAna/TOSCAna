# TOSCAna Docker image

[![ImageLayers Size](https://img.shields.io/imagelayers/image-size/toscana/toscana/latest.svg)](https://hub.docker.com/r/toscana/toscana/)

This Docker image allows you to run the TOSCAna transformer without having to handle the dependencies.

## Running the Docker image

To just launch the Application the following command can be used:
```bash
docker run -d -p 8080:8080 toscana/toscana:latest
```

This will launch the transformer on Port 8080. Transformation to Kubernetes and AWS will not work, because the dependencies are not enabled.

### Supporting Kubernetes

To support the Kubernetes Plugin, a running Docker daemon is required. This Requirement can either be fulfiled by running Docker in Docker (DinD) or by Linking the local Docker daemons socket via a volume.

#### Option 1: Running with the Hosts `dockerd`

Running with the Hosts docker daemon is achieved by linking the unix socket `/var/run/docker.sock` as a volume to the same path in the container

```bash
docker run -d -p 8080:8080 -v /var/run/docker.sock:/var/run/docker.sock toscana/toscana:latest
```

#### Option 2: Running Docker in Docker

You can also run a second instance of the docker daemon within the container (Docker in Docker). Because Docker in Docker needs privileged access the container has to be launched using the `--privileged` flag, if the privileged flag is missing, the image will launch but transformations to kubernetes are not possible:

```bash
docker run -d -p 8080:8080 --privileged toscana/toscana:latest-dind
```

### Setting AWS Credentials

To use the complete range of functions of the AWS Plugin, you have to supply your AWS Credentials to the Container. This can be done by by mounting your local AWS CLI configuration folder as a volume, this assumes you have set the credentials on the host.

#### Mounting the AWS Folder as a volume

```bash
docker run -d -p 8080:8080 -v ~/.aws:/root/.aws toscana/toscana:latest
```

## Mount the Datadirectory as a Volume

Both the DinD Image and the regular Docker Image use the folder `/toscana/data` as the datadirectory this directory can either be linked to a folder on the host:
```bash
docker run -d -p 8080:8080 -v <HOST-FOLDER>:/toscana/data -v /var/run/docker.sock:/var/run/docker.sock toscana/toscana:latest
```
Or the volume can be named by calling:
```bash
docker run -d -p 8080:8080 -v my_toscana_data_volume:/toscana/data -v /var/run/docker.sock:/var/run/docker.sock toscana/toscana:latest
```
