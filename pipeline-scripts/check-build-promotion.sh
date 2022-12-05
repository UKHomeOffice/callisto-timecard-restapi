LAST_BUILD=$(
  drone build ls --format '{{.Number}}' --branch "$DRONE_BRANCH" --status success \
  --limit 100 "$DRONE_REPO" | head -n 1
)
if [ $LAST_BUILD != $DRONE_BUILD_PARENT ]; then
  echo 'Could not promote as last build: ' $LAST_BUILD ' was not successful';
  exit 1;
fi