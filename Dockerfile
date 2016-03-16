FROM clojure
MAINTAINER Rackspace Managed Security <rms-engineering@rackspace.com>

RUN apt-get update && apt-get upgrade -y && apt-get install -y npm

# Build desdemona
RUN mkdir -p /usr/src/desdemona
WORKDIR /usr/src/desdemona
COPY project.clj /usr/src/desdemona/
RUN lein deps
COPY . /usr/src/desdemona
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" /srv/desdemona.jar

RUN mkdir /etc/service/onyx_peer
ADD script/run_peers.sh /etc/service/onyx_peer/run
RUN mkdir /etc/service/aeron
ADD script/run_aeron.sh /etc/service/aeron/run

EXPOSE 40200/tcp
EXPOSE 40200/udp

CMD ["/sbin/my_init"]
