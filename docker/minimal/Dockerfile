FROM openjdk:8-jdk-alpine
MAINTAINER c-muelller (cmueller.dev@gmail.com)
WORKDIR /toscana
RUN apk update && \
    apk add maven git && \
    git clone https://github.com/StuPro-TOSCAna/TOSCAna.git && \ 
    cd TOSCAna && \
    mvn install -DskipTests && \
    cd .. && \
    cp TOSCAna/server/target/server-1.0-SNAPSHOT.jar server.jar && \
    rm -r TOSCAna && \
    apk del maven git && \
    rm -r /root/.m2/
EXPOSE 8080
ENV DATADIR="/toscana/data"
ENV SERVER_PORT="8080"
VOLUME ["/toscana/data"]
ENTRYPOINT java -jar server.jar
