# Dockerfile Builder

When transforming a CSAR to a Docker based Platform like, Kubernetes a Dockerfile is needed to describe the necessary build steps. To simplify the creation of Dockerfiles a Builder class for these should be provided.

## Design of the Dockerfile Builder

The DockerfileBuilder will follow a similiar pattern to the `StringBuilder` included in the `java.lang` package.
This includes the possibility to chain the methods like this:

```java
StringBuilder b = new StringBuilder();
b.append("hello").append(" ").append("world");
```

Of course the DockerfileBuilder does not consist of one method (beeing append) it will feature the following methods/operations:

- Set the Base Image (Will probably be done using the constructor (**REQUIRED**))
- Execute a Shell/Bash command
- Copy a File, multiple Files or a Folder into the working Directory of the image (on the disk of the transformer) -> This call also includes a `ADD` or `COPY` statement in the Dockerfile
- Expose Ports (`EXPOSE` Command)
- Set the working Directory in the image (`WORKDIR` Command)
- Set a Volume (`VOLUME` Command)
- Set the Entrypoint (`ENTRYPOINT` Command)
- Option to just wite the Dockerfile into the working Directory
- 
