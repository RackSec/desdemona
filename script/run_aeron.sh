#!/bin/sh

echo "Setting shared memory for Aeron"
mount -t tmpfs -o remount,rw,nosuid,nodev,noexec,relatime,size=256M tmpfs /dev/shm
APP_NAME=$(echo "desdemona" | sed s/"-"/"_"/g)

exec java -cp /srv/desdemona.jar "$APP_NAME.launcher.aeron_media_driver" >>/var/log/aeron.log 2>&1
