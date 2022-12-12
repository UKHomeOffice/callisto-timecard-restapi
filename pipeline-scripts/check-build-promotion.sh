LAST_SUCCESSFUL_BUILD=$(
  drone build ls --format '{{.Number}}' --branch "$DRONE_BRANCH" --status success \
  --limit 100 "$DRONE_REPO" | head -n 1
)
if [ $LAST_SUCCESSFUL_BUILD != $DRONE_BUILD_PARENT ]; then
  echo 'Could not promote as build: ' $DRONE_BUILD_PARENT ' was not successful';
  exit 1;
fi