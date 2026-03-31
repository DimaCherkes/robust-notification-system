#!/bin/sh

PROFILE=${PROFILE:-docker}

echo "Starting service with profile: $PROFILE"
exec java -jar /srv/decision-service.jar --spring.profiles.active=$PROFILE
