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

#if no create topic

#  if yes check if topic has acls

#    if no create acls

#  if yes exit