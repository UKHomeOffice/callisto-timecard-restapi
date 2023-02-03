set -e
keystore_password=$1

cd scripts
ls -ltr

echo "Starting kafka.sh"
#put password in timecard-properties
sed 's/#/'$keystore_password'/g' timecard-properties > timecard-properties-temp && mv timecard-properties-temp timecard-properties

source create-topic.sh
create_topic
apply_permissions