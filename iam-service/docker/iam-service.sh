#!/bin/sh

PROFILE=${PROFILE:-docker}

echo "Starting service with profile: $PROFILE"
exec java -jar /srv/iam-service.jar --spring.profiles.active=$PROFILE
