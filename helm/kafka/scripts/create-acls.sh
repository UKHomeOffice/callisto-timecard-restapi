#!/usr/bin/env bash
set -e

root_path=${BASH_SOURCE[0]%/*}
. $root_path/create-topic.sh

bootstrap_server=$1
keystore_password=$2
properties_file=/tmp/timecard-properties

cp scripts/timecard-properties tmp/
cd /tmp

#put password in timecard-properties
if sed 's/#/'$keystore_password'/g' timecard-properties > timecard-properties-temp && mv timecard-properties-temp timecard-properties
then
  echo "Properties file updated"
else
  echo "Properties file failed to update"
  exit 1
fi

ensure_topic_exists $3
apply_permissions