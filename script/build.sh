#!/bin/bash
set -e
lein clean
lein uberjar
docker build -t desdemona:0.1.0 .
