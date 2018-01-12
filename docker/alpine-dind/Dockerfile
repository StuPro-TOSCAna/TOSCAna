# This image is based on the Docker in Docker image (needs --privileged to work correctly)
# If the privileged flag is missing this image will not support kubernetes based transformations
FROM docker:dind
LABEL maintainer="Christian Müller <cmueller.dev@gmail.com>"
WORKDIR /toscana
RUN apk add --update --no-cache openjdk8 maven git bash && \
    git clone https://github.com/StuPro-TOSCAna/TOSCAna.git && \
    cd TOSCAna && \
    mvn install -DskipTests -P build && \
    cd .. && \
    cp TOSCAna/server/target/server-1.0-SNAPSHOT.jar server.jar && \
    rm -r TOSCAna && \
    apk del maven git && \
    rm -r /root/.m2/
EXPOSE 8080
ENV DATADIR="/toscana/data"
ENV SERVER_PORT="8080"
ADD toscana-dind-entrypoint.sh .
ENTRYPOINT ./toscana-dind-entrypoint.sh
