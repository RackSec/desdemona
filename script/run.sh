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

exec lein pdo \
     run -m desdemona.launcher.aeron-media-driver, \
     run -m desdemona.launcher.launch-prod-peers $N_PEERS
