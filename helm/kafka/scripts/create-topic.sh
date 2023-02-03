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

echo "Checking if topic is available"
echo $bootstrap_server
if kafka-topics.sh --bootstrap-server $bootstrap_server --command-config $properties_file \
        --create --topic $topic --if-not-exists \
        > /dev/null
then
  echo Topic available: $topic
fi

# read through the contents of the permissions file and
# create the permissions. Ignore empty lines and comments
IFS=$'\n' acl_config=$(grep --color=never "^[^#].*" $root_path/permissions.txt)
unset IFS
for line in "${acl_config[@]}"
do
# skip empty lines
if [ -z "$line" ]; then continue; fi
details=$line

# if first argument is --topic assume a new list of permissions are being specified
if [ "${details[0]}" = "--topic" ]
then
# if permissions have already been specified for a previous topic
# apply them.
if [ -n "$topic" ]
then
  IFS=$'\n'
  set_permissions $topic $pattern_type "${permissions[*]/%/$'\n'}"
  unset IFS
fi
  # Reset the variables
  topic=${details[1]}
  pattern_type=${details[3]}
  permissions=
  else

    # Add the desired permisions to the current permission
    # array for this topic
    principal=${details[0]}
    operation=${details[1]}
    permission=${details[2]}

    permissions+="$principal $operation $permission"
  fi

done
    # The end of the file has been reached. If a topic
    # was set apply the permissions.
    if [ -n "$topic" ]
    then
        IFS=$'\n'
        set_permissions $topic $pattern_type "${permissions[*]/%/$'\n'}"
        unset IFS
    fi
