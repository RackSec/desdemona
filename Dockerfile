FROM clojure
MAINTAINER Rackspace Managed Security <rms-engineering@rackspace.com>

# npm would not be necessary with a separate build container; it is only used
# for testing. However, omitting it currently breaks the build. See issues:
# https://github.com/RackSec/desdemona/issues/79
# https://github.com/RackSec/desdemona/issues/76
RUN apt-get update && apt-get upgrade -y && apt-get install -y npm

RUN mkdir -p /usr/src/desdemona
WORKDIR /usr/src/desdemona

# Cache the dependencies in a Docker fs layer.
COPY project.clj /usr/src/desdemona/
RUN lein deps

# Add Desdemona to the Docker container.
COPY . /usr/src/desdemona

RUN lein uberjar
RUN mv target/desdemona-*-standalone.jar /srv/desdemona.jar

EXPOSE 40200/tcp
EXPOSE 40200/udp

CMD ["script/run.sh"]
