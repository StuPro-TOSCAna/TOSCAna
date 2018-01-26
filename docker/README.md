# TOSCAna Docker image

[![Docker Build Status](https://img.shields.io/docker/build/toscana/toscana.svg)](https://hub.docker.com/r/toscana/toscana/) [![ImageLayers Size](https://img.shields.io/imagelayers/image-size/toscana/toscana/latest.svg)](https://hub.docker.com/r/toscana/toscana/) 

This Docker image allows you to run the TOSCAna transformer without having to handle the dependencies.

## Running the Docker image

To just launch the Application the following command can be used:
```
docker run -d -p 8080:8080 toscana/toscana:latest
```

This will launch the transformer on Port 8080. Transformation to Kubernetes and AWS will not work, because the dependencies are not enabled.

### Running Docker in Docker

In order to enable the Transformation to Kubernetes the Container has to run a Instance of the Docker Daemon (aka. `dockerd`). Because Docker in Docker needs privileged access the container has to be launched using the `--privileged` flag:

```
docker run -d -p 8080:8080 --privileged toscana/toscana:latest
```

### Setting AWS Credentials

To use the complete range of functions of the AWS Plugin, you have to supply your AWS Credentials to the Container. This can either be done by setting the environment variables within the Container or by mounting your local AWS CLI configuration folder as a volume, this assumes you have set the credentials on the host.

#### Option 1: Mounting the AWS Folder as a volume

```
docker run -d -p 8080:8080 -v ~/.aws:/root/.aws toscana/toscana:latest
```

#### Option 2: Setting the Credentials using environment variables

## Mount the Datadirectory as a Volume
