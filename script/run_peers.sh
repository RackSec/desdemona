#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

export BIND_ADDR
BIND_ADDR=$(hostname --ip-address)
exec java -cp /srv/desdemona.jar "desdemona.launcher.launch_prod_peers" $N_PEERS
