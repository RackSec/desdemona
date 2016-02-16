FROM debian:latest
MAINTAINER Andras Mitzki <andras.mitzki@balabit.com>

RUN apt-get update -qq && apt-get install -y \
    wget

RUN wget -qO - http://download.opensuse.org/repositories/home:/laszlo_budai:/syslog-ng/Debian_8.0/Release.key | apt-key add -
RUN echo 'deb http://download.opensuse.org/repositories/home:/laszlo_budai:/syslog-ng/Debian_8.0 ./' | tee --append /etc/apt/sources.list.d/syslog-ng-obs.list

RUN apt-get update -qq && apt-get install -y \
    syslog-ng

ADD openjdk-libjvm.conf /etc/ld.so.conf.d/openjdk-libjvm.conf
RUN ldconfig

RUN wget -q http://artfiles.org/apache.org/kafka/0.9.0.0/kafka_2.11-0.9.0.0.tgz -O /tmp/kafka_2.11-0.9.0.0.tgz
RUN tar xfz /tmp/kafka_2.11-0.9.0.0.tgz -C /opt
RUN ln -s /opt/kafka_2.11-0.9.0.0 /opt/kafka

EXPOSE 514
EXPOSE 601
EXPOSE 6514

ENTRYPOINT ["/usr/sbin/syslog-ng", "-F"]
