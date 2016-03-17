#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

export BIND_ADDR
BIND_ADDR=$(hostname --ip-address)

echo "Setting shared memory for Aeron"
mount -t tmpfs \
      -o remount,rw,nosuid,nodev,noexec,relatime,size=256M \
      tmpfs /dev/shm

java -cp /srv/desdemona.jar desdemona.launcher.aeron_media_driver 2>&1 &
java -cp /srv/desdemona.jar desdemona.launcher.launch_prod_peers $N_PEERS
