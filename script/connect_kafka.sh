#!/bin/bash

echo '*** Connecting to Kafka container... (this will execute in the foreground)'
echo '*** Type some things below to send them to Kafka.'
echo -n '> '
docker run --rm -it --link desdemona_kafka_1:kafka1 wurstmeister/kafka bash -c "\$KAFKA_HOME/bin/kafka-console-producer.sh --topic test1 --broker-list=kafka1:9092"
