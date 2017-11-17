FROM openjdk:8-jre-alpine
MAINTAINER c-muelller (cmueller.dev@gmail.com)
WORKDIR /toscana
COPY server.jar server.jar
ENV DATADIR="/toscana/data"
ENV SERVER_PORT="8080"
ENV ARGS=""
VOLUME ["/toscana/data"]
EXPOSE 8080
ENTRYPOINT java -jar server.jar $ARGS
