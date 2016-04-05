#!/bin/bash
set -x
docker-compose build
docker-compose up -d
sleep 5
LOOP_CONTAINER_ID=`docker ps | grep "lein test" | awk '{ print $1}'`
TEST_CONTAINER_ID=$LOOP_CONTAINER_ID
COUNTER=1
while [ "${LOOP_CONTAINER_ID}" != '' ]; do
  if [ "${COUNTER}" -gt "60" ]; then
    # Took longer than a minute. Probably stuck.
    exit 1
  fi
  sleep 1
  COUNTER=$((COUNTER+1))
  LOOP_CONTAINER_ID=`docker ps | grep "lein test" | awk '{ print $1}'`
done

docker-compose stop
docker logs $TEST_CONTAINER_ID
exit `docker inspect -f {{.State.ExitCode}} $TEST_CONTAINER_ID`
