#!/usr/bin/env bash
tearDown() {

    # kill anything that is running
    dockerContainers=$(docker ps -q)
    if [[ "$dockerContainers" == "" ]]; then
       echo "No previously running containers.. nothing to kill"
    else
       echo "Killing docker containers.. $dockerContainers"
       docker kill ${dockerContainers}
    fi

    # This really cleans everything up so there's nothing previous that could contaminate
    echo "Pruning docker containers/images"
    docker system prune -a -f

    if [[ -d dev-env-develop ]]; then
      echo "Tearing down existing dev-env-develop directory"
      rm -rf dev-env-develop
    fi
}

outputVersions() {
  echo "TARGET_ENV=$TARGET_ENV"
  echo "LA_VERSION=$LA_VERSION"
  echo "UM_VERSION=$UM_VERSION"
  echo "BB_VERSION=$BB_VERSION"
  echo "AP_VERSION=$AP_VERSION"
  echo "AZ_VERSION=$AZ_VERSION"
  echo "MG_VERSION=$MG_VERSION"
  echo "RD_VERSION=$RD_VERSION"
  echo "PR_VERSION=$PR_VERSION"
}

set -a

if [[ ! -e ~/.ssh/github_token ]]; then
  echo "You need to create a personal access github token in ~/.ssh/github_token in order to access github"
  exit 1
fi

# Cleanup existing containers
tearDown

# Get the dev-env stuff
echo "Retrieving dev-env (develop) scripts."
curl -sL -H "Authorization: token $(cat ~/.ssh/github_token)" https://github.com/uk-gov-dft/dev-env/archive/develop.tar.gz | tar xz
if [ $? -ne 0 ]; then
   echo "Cannot download dev-env!"
   exit 1
fi

# 'VERSION-computed' needed for environment variables
gradle :outputComputedVersion

. dev-env-develop/env.sh
if ! [[ "$BRANCH_NAME" =~ ^develop.*|^release.* ]]; then
   . env-feature.sh
fi

outputVersions

cd dev-env-develop
bash load-modules.sh
docker-compose build
docker-compose up -d --no-color
./wait_for_it.sh localhost:2222 localhost:5432 localhost:8681:/manage/actuator/health localhost:8381:/manage/actuator/health localhost:8281:/manage/actuator/health localhost:8081:/manage/actuator/health localhost:8481:/manage/actuator/health localhost:8181:/manage/actuator/health localhost:8581:/manage/actuator/health
psql -h localhost -U developer -d bb_dev -f ./scripts/db/setup-users.sql

# Required for running acceptance tests
ssh-keyscan -p 2222 localhost > ~/.ssh/sftp_known_hosts

# Run the acceptance tests
cd ..
gradle acceptanceTests
testExitCode=$?

# Save the logs if something went wrong
if [[ "$testExitCode" -ne 0 ]]; then
   cd dev-env-develop
   docker-compose logs -t --no-color > ../docker.log
   cd ..
fi

# Tear down
tearDown

echo "Exiting with code:$testExitCode"

exit "$testExitCode"

