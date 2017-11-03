FROM openjdk:8-jdk
MAINTAINER c-muelller (cmueller.dev@gmail.com)
WORKDIR /toscana
RUN apt-get update && \
    apt-get install -y maven git && \
    git clone https://github.com/StuPro-TOSCAna/TOSCAna.git && \ 
    cd TOSCAna && \
    mvn install -DskipTests && \
    cd .. && \
    cp TOSCAna/core/target/core-1.0-SNAPSHOT.jar core.jar && \
    rm -r TOSCAna && \
    apt-get autoremove -y maven git 
EXPOSE 8080
ENV DATADIR="/toscana/data"
VOLUME ["/toscana/data"]
ENTRYPOINT java -jar core.jar
