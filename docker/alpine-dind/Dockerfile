# This image is based on the Docker in Docker image (needs --privileged to work correctly)
# If the privileged flag is missing this image will not support kubernetes based transformations
FROM docker:dind
LABEL maintainer="Christian Müller <cmueller.dev@gmail.com>"
ENV LANG=C.UTF-8
WORKDIR /toscana
ADD *.sh /toscana/
RUN apk add --update --no-cache bash
RUN ./install-deps.sh
RUN ./build-toscana.sh
RUN ./cleanup.sh
EXPOSE 8080
ENV DATADIR="/toscana/data"
ENV SERVER_PORT="8080"
ENTRYPOINT ./toscana-dind-entrypoint.sh
