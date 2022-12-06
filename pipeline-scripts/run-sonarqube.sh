mvn -s ./timecard_settings.xml sonar:sonar \
  -Dsonar.host.url=$${SONAR_HOST} \
  -Dsonar.login=$${SONAR_TOKEN} \
  -Dsonar.organization=ukhomeoffice \
  -Dsonar.projectKey=callisto-timecard-restapi \
  -Dsonar.branch.name=$DRONE_BRANCH \
  -Dsonar.projectName=callisto-timecard-restapi \
  -Dsonar.qualitygate.wait=true