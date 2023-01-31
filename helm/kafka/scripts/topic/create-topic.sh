set -e

service_alias=$1
topic=$2
timecard_dir=/timecard-topic
bootstrap_server=$3

# Read contents of topics.txt and ensure each topic exists
function create_topics(){
  ensure_topic_exists
}

# Ensures a topic exists
ensure_topic_exists() {
    kafka-topics.sh --bootstrap-server $bootstrap_server --command-config ./$service_alias-topic/$service_alias-properties  \
        --create --topic $topic --if-not-exists \
        > /dev/null

    echo Topic available: $topic
}

function apply_permissions() {
    local acl_config line
    local details pattern_type
    local permissions principal operation permission

    # read through the contents of the permissions file and
    # create the permissions. Ignore empty lines and comments
    IFS=$'\n' acl_config=( $(grep --color=never "^[^#].*" $timecard_dir/permissions.txt) )
    unset IFS
    for line in "${acl_config[@]}"
    do
      # skip empty lines
      if [ -z "$line" ]; then continue; fi
      details=($line)

      # if first argument is --topic assume a new list of permissions are being specified
        if [ "${details[0]}" = "--topic" ]
        then
        # if permissions have already been specified for a previous topic
        # apply them.
          if [ -n "$topic" ]
          then
            IFS=$'\n' set_permissions $topic $pattern_type "${permissions[*]/%/$'\n'}"
            unset IFS
          fi
            # Reset the variables
            topic=${details[1]}
            pattern_type=${details[3]}
            permissions=()
          else
            # Add the desired permisions to the current permission
            # array for this topic
            principal=${details[0]}
            operation=${details[1]}
            permission=${details[2]}

            permissions+=("$principal $operation $permission")
          fi
    done
}

#if no create topic

#  if yes check if topic has acls

#    if no create acls

#  if yes exit