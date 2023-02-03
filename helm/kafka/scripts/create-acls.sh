#!/usr/bin/env bash
set -e

root_path=${BASH_SOURCE[0]%/*}
. $root_path/create-topic.sh

bootstrap_server=$1
keystore_password=$2

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

ensure_topic_exists $bootstrap_server
apply_permissions