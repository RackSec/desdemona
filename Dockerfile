FROM phusion/baseimage:0.9.17
MAINTAINER Rackspace Managed Security <rms-engineering@rackspace.com>

RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get install -y software-properties-common
RUN apt-get update

# Auto-accept the Oracle JDK license
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

RUN mkdir /etc/service/onyx_peer
RUN mkdir /etc/service/aeron

RUN apt-get install -y oracle-java8-installer

RUN apt-get install -y npm

# Install lein -- https://github.com/Quantisan/docker-clojure/blob/master/Dockerfile
ENV LEIN_VERSION=2.5.3
ENV LEIN_INSTALL=/usr/local/bin/

WORKDIR /tmp

RUN mkdir -p $LEIN_INSTALL \
  && wget --quiet https://github.com/technomancy/leiningen/archive/$LEIN_VERSION.tar.gz \
  && echo "Comparing archive checksum ..." \
  && echo "871d2e308076d2e9edf457cffc9d15996c8d003e *$LEIN_VERSION.tar.gz" | sha1sum -c - \

  && mkdir ./leiningen \
  && tar -xzf $LEIN_VERSION.tar.gz  -C ./leiningen/ --strip-components=1 \
  && mv leiningen/bin/lein-pkg $LEIN_INSTALL/lein \
  && rm -rf $LEIN_VERSION.tar.gz ./leiningen \

  && chmod 0755 $LEIN_INSTALL/lein \

  && wget --quiet https://github.com/technomancy/leiningen/releases/download/$LEIN_VERSION/leiningen-$LEIN_VERSION-standalone.zip \
  && wget --quiet https://github.com/technomancy/leiningen/releases/download/$LEIN_VERSION/leiningen-$LEIN_VERSION-standalone.zip.asc \

  && gpg --keyserver pool.sks-keyservers.net --recv-key 2E708FB2FCECA07FF8184E275A92E04305696D78 \
  && echo "Verifying Jar file signature ..." \
  && gpg --verify leiningen-$LEIN_VERSION-standalone.zip.asc \

  && rm leiningen-$LEIN_VERSION-standalone.zip.asc \
  && mv leiningen-$LEIN_VERSION-standalone.zip /usr/share/java/leiningen-$LEIN_VERSION-standalone.jar \

  &&  apt-get update && apt-get install rlfe && rm -rf /var/lib/apt/lists/*

ENV PATH=$PATH:$LEIN_INSTALL
ENV LEIN_ROOT 1

# Build desdemona
RUN mkdir -p /usr/src/desdemona
WORKDIR /usr/src/desdemona
COPY project.clj /usr/src/desdemona/
RUN lein deps
COPY . /usr/src/desdemona
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" /srv/desdemona.jar

ADD script/run_peers.sh /etc/service/onyx_peer/run
ADD script/run_aeron.sh /etc/service/aeron/run

EXPOSE 40200/tcp
EXPOSE 40200/udp

CMD ["/sbin/my_init"]
