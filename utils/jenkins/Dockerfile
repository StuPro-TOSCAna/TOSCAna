FROM docker:dind
LABEL maintainer="Christian Müller <cmueller.dev@gmail.com>"
WORKDIR /toscana
RUN apk add --update --no-cache openjdk8-jre bash
COPY server.jar server.jar
ENV DATADIR="/toscana/data"
ENV SERVER_PORT="8080"
VOLUME ["/toscana/data"]
EXPOSE 8080
ADD toscana-dind-entrypoint.sh .
ENTRYPOINT ./toscana-dind-entrypoint.sh
