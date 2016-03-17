FROM clojure
MAINTAINER Rackspace Managed Security <rms-engineering@rackspace.com>

RUN apt-get update && apt-get upgrade -y && apt-get install -y npm

# Build desdemona
COPY . /usr/src/desdemona
WORKDIR /usr/src/desdemona

# Cache the dependencies.
# RUN lein deps

RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" /srv/desdemona.jar

RUN mkdir /etc/service/onyx_peer
COPY script/run_peers.sh /etc/service/onyx_peer/run
RUN mkdir /etc/service/aeron
COPY script/run_aeron.sh /etc/service/aeron/run

EXPOSE 40200/tcp
EXPOSE 40200/udp

CMD ["/sbin/my_init"]
