FROM openjdk:8-jdk-alpine
MAINTAINER c-muelller (cmueller.dev@gmail.com)
WORKDIR /toscana
RUN apk update && \
    apk add maven git && \
    git clone https://github.com/StuPro-TOSCAna/TOSCAna.git && \ 
    cd TOSCAna && \
    mvn install -DskipTests && \
    cd .. && \
    cp TOSCAna/core/target/core-1.0-SNAPSHOT.jar core.jar && \
    rm -r TOSCAna && \
    apk del maven git && \
    rm -r /root/.m2/
EXPOSE 8080
ENV DATADIR="/toscana/data"
VOLUME ["/toscana/data"]
ENTRYPOINT java -jar core.jar
