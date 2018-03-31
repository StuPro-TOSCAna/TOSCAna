# Dockerfile Builder

When transforming a CSAR to a docker-based platform like Kubernetes, a Dockerfile is needed to describe the necessary build steps.  
To simplify the creation of such Dockerfiles, a builder class for these is provided.

## Design of the Dockerfile Builder

The DockerfileBuilder follows a similar design as the `StringBuilder` included in the `java.lang` package.
This includes the possibility to chain methods like this:

```java
StringBuilder b = new StringBuilder();
b.append("hello").append(" ").append("world");
```

Of course the DockerfileBuilder does not consist of one method (being append). it features the following methods/operations:

 - Set the base image (using the constructor)
 - Execute a shell/bash command
 - Copy a file, multiple files or a folder into the working directory of the image (on the disk of the server) -> This call also includes an `ADD` or `COPY` statement in the Dockerfile
 - Expose ports (`EXPOSE` Command)
 - Set the working directory in the image (`WORKDIR` Command)
 - Set a volume (`VOLUME` Command)
 - Set the entry point (`ENTRYPOINT` Command)
 - Option to finally write the Dockerfile to the working directory
