set -e

# Read contents of topics.txt and ensure each topic exists
function create_topics(){
    local topics
    local topic

    # This could be improved by reading a list of all topics
    # once and skipping any in the list as
    # calling the kafka broker is slow
    IFS=$'\n' topics=( $(cat $root_path/topics.txt) )
    unset IFS
    for topic in "${topics[@]}"
    do
        ensure_topic_exists $topic
    done
}


#if no create topic

#  if yes check if topic has acls

#    if no create acls

#  if yes exit