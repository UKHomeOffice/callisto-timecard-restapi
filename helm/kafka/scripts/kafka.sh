set -e
service_alias=$1
bootstrap_url=$2
keystore_dir=$3
properties_file=$topic_dir/timecard-properties
keystore_password=$1

cd $keystore_dir
cp /scripts/timecard-properties $keystore_dir
ls -ltr

echo "Starting kafka.sh"
#put password in timecard-properties
sed 's/#/'$keystore_password'/g' timecard-properties > timecard-properties-temp && mv timecard-properties-temp timecard-properties

source ./create-topic.sh
create_topic callisto-timecard
apply_permissions