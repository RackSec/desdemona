#!/bin/bash
set -e

echo '*** Running `lein clean`...'
lein clean

echo '*** Running `lein uberjar`...'
lein uberjar

echo '*** Building docker image...'
docker build -t desdemona:0.1.0 .

echo '*** Success.'
