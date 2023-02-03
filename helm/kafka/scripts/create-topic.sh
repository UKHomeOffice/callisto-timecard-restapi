#!/bin/bash

set -e

properties_file=/tmp/timecard-properties
bootstrap_server=$1
keystore_password=$2
topic=callisto-timecard
# Ensures a topic exists

#put password in timecard-properties
sed 's/#/'$keystore_password'/g' timecard-properties > timecard-properties-temp && mv timecard-properties-temp timecard-properties

echo "Checking if topic is available"
if kafka-topics.sh --bootstrap-server $bootstrap_url--command-config $properties_file \
        --create --topic $topic --if-not-exists \
        > /dev/null
then
  echo Topic available: $topic
fi