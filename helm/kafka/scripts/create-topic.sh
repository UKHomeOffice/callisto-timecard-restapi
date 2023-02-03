#!/bin/bash

set -e

properties_file=/tmp/timecard-properties
bootstrap_server=$1
keystore_password=$2
topic=callisto-timecard
# Ensures a topic exists

cp scripts/timecard-properties tmp/
cd /tmp

#put password in timecard-properties
if sed 's/#/'$keystore_password'/g' timecard-properties > timecard-properties-temp && mv timecard-properties-temp timecard-properties
then
  echo "properties file updated"
else
  echo "properties file failed to update"
  exit 1
fi

cd /bin/sh
echo "Checking if topic is available"
echo $bootstrap_server
if kafka-topics.sh --bootstrap-server $bootstrap_server --command-config $properties_file \
        --create --topic $topic --if-not-exists \
        > /dev/null
then
  echo Topic available: $topic
fi