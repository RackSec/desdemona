#!/bin/bash
set -e

echo '*** Submitting sample job...'
ZOOKEEPER=$(echo $DOCKER_HOST|cut -d ':' -f 2|sed "s/\/\///g") lein run -m desdemona.jobs.sample-submit-job

echo '*** Success.'
