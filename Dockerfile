FROM phusion/baseimage:0.9.17
MAINTAINER Michael Drogalis <mjd3089@rit.edu>

RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get install -y software-properties-common
RUN apt-get update

# Auto-accept the Oracle JDK license
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

RUN apt-get install -y oracle-java8-installer

ADD target/desdemona-0.1.0-SNAPSHOT-standalone.jar /srv/desdemona.jar

ADD script/run-peers.sh /srv/run-peers.sh

ENTRYPOINT ["/bin/sh", "/srv/run-peers.sh"]
