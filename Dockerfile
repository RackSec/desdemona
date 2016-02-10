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

ADD target/desdemona-0.1.0-SNAPSHOT-standalone.jar /srv/desdemona.jar

ADD script/run_peers.sh /etc/service/onyx_peer/run
ADD script/run_aeron.sh /etc/service/aeron/run

EXPOSE 40200/tcp
EXPOSE 40200/udp

CMD ["/sbin/my_init"]
