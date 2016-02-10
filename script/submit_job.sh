#!/bin/bash
ZOOKEEPER=$(echo $DOCKER_HOST|cut -d ':' -f 2|sed "s/\/\///g") lein run -m desdemona.jobs.sample-submit-job
