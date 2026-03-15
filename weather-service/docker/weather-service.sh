#!/bin/sh

PROFILE=${PROFILE:-docker}

echo "Starting service with profile: $PROFILE"
exec java -jar /srv/weather-service.jar --spring.profiles.active=$PROFILE
