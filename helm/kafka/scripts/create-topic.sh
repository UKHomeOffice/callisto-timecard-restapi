#!/bin/bash

set -e


# Method for retrieving current ACL for given topic and pattern type
# Example: Get permissions for topic prefix timecard
# get_current_acl timecard prefixed
# Example: Get permissions for topic timecard
# get_current_acl timecard literal
get_current_acl() {

    local topic=$1
    local type=$2
    local acls

    # Call the kafka-acl script with relevant arguments and grep only the lines
    # form the output with the principals. Then use sed to strip out everything
    # but the required values leaving us with a space delimited list of existing
    # permissions
    IFS=$'\n' acls=( $(kafka-acls.sh --bootstrap-server $bootstrap_server --command-config $properties_file --list --topic $topic --resource-pattern-type $type | grep -o -e '(principal.*)' | sed -E 's/.*principal=(.*), host=\*, operation=(.*), permissionType=(.*)\)/\1 \2 \3/') )

    echo "${acls[*]/%/$'\n'}"

}

# Function to set the ACL for the given topic and pattern type
# to the specified ACL
set_permissions() {

    local topic=$1
    local type=$2
    local desired_permissions=($3)
    local existing_permissions=()
    local set details principal operation permission

    echo Applying desired permissions for $topic $type

    # get the current ACL for given topic and pattern type
    local current_acl=($(get_current_acl $topic $type))

    # Iterate the current ACL to see if each existing permission
    # is still required. Remove any that are not desired
    IFS=' ' # set the input field separator
    for set in "${current_acl[@]}"
    do
        details=($set)
        principal=${details[0]}
        operation=${details[1]}
        permission=${details[2]}

        # Do the desired permissions contain this existing permission
        if [[ ! " ${desired_permissions[*],,} " =~ " ${principal,,} ${operation,,} ${permission,,} " ]]
        then
            echo Removing: ${principal} ${operation} ${permission}
            kafka-acls.sh --bootstrap-server $bootstrap_server \
                --command-config $properties_file \
                --topic $topic --resource-pattern-type $type \
                --remove --force \
                --${permission,,}-principal $principal --operation $operation \
                > /dev/null
        else
            # Permission already exists so store for later so that we can
            # skip it to save time as it already exists
            echo Skipping: $principal $operation $permission
            existing_permissions+=("$principal $operation $permission")
        fi
    done
    unset IFS

    # Iterate all of the desired permissions and create them
    # if they don't already exist
    for set in "${desired_permissions[@]}"
    do
        details=($set)
        principal=${details[0]}
        operation=${details[1]}
        permission=${details[2]}

        # If the desired permission doesn't exist, add it
        if [[ ! " ${existing_permissions[*],,} " =~ " ${principal,,} ${operation,,} ${permission,,} " ]]
        then
            echo Adding: ${principal} ${operation} ${permission}
            kafka-acls.sh --bootstrap-server $bootstrap_server\
                --command-config $properties_file \
                --topic $topic --resource-pattern-type $type \
                --add --force \
                --${permission,,}-principal $principal --operation $operation \
                > /dev/null
        fi
        echo "Success!!"
    done
}

# Method to apply permissions set in permissions.txt file
function apply_permissions() {

    local acl_config line
    local details topic pattern_type
    local permissions principal operation permission

    # read through the contents of the permissions file and
    # create the permissions. Ignore empty lines and comments
    IFS=$'\n' acl_config=( $(grep --color=never "^[^#].*" $root_path/permissions.txt) )
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
                IFS=$'\n'
                set_permissions $topic $pattern_type "${permissions[*]/%/$'\n'}"
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

    # The end of the file has been reached. If a topic
    # was set apply the permissions.
    if [ -n "$topic" ]
    then
        IFS=$'\n'
        set_permissions $topic $pattern_type "${permissions[*]/%/$'\n'}"
        unset IFS
    fi
}

# Ensures a topic exists
ensure_topic_exists() {
    local topic=$1

    kafka-topics.sh --bootstrap-server $bootstrap_server --command-config $properties_file \
        --create --topic $topic --if-not-exists \
        > /dev/null

    echo Topic available: $topic
}