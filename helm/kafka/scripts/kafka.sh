#!/bin/bash

set -e
bootstrap_server=$1
keystore_password=$2

cd /tmp
cp /scripts/timecard-properties /tmp

echo "Starting kafka.sh"
#put password in timecard-properties
sed 's/#/'$keystore_password'/g' timecard-properties > timecard-properties-temp && mv timecard-properties-temp timecard-properties

cd /scripts
. ./create-topic.sh
create_topic $bootstrap_server
apply_permissions