# Getting started

### Using Docker
You can use docker to get TOSCAna running in next to no time. Simply run:
```
docker run -d -p 8080:8080 toscana/toscana:latest
```
Afterwards, you can access the web application at [localhost:8080](http://localhost:8080).

For further details on running TOSCAna with docker, read [here](docker.md).


### Manual installation
```sh
git clone https://github.com/StuPro-TOSCAna/TOSCAna.git
cd TOSCAna/server
mvn clean install -Dmaven.test.skip=true
```

### Starting the server
```sh
java -jar target/*.jar
```
The web application is per default bound to [localhost:8084](http://localhost:8084).


# Configuration

### Web app port
Per default, TOSCAna's web application is served on port 8084. This can be changed by specifying `--server.port=<port>` at startup.

### Data location
TOSCAna persists CSARs and transformations to disk. The default location is:

- *unix & osx*: `~/.toscana`
- *windows*: `%userprofile%\AppData\toscana`

This can be changed with the startup flag `--datadir <path>`.
