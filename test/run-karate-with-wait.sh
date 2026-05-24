#!/usr/bin/env bash
set -euo pipefail

# wait-and-run script: waits for API to be reachable then runs Karate tests
# Usage: ./run-karate-with-wait.sh [baseUrl] [timeoutSeconds]

BASE_URL=${1:-http://localhost:8080/home-pulse}
TIMEOUT=${2:-60}
SLEEP_INTERVAL=2

echo "Waiting for API at ${BASE_URL} (timeout: ${TIMEOUT}s)"
end=$((SECONDS+TIMEOUT))
while [ $SECONDS -le $end ]; do
  if curl -s --connect-timeout 2 -o /dev/null "${BASE_URL}" ; then
    echo "API is reachable. Running Karate tests..."
    mvn -Dkarate.baseUrl=${BASE_URL} -f test/pom.xml test
    exit $?
  fi
  printf '.'
  sleep ${SLEEP_INTERVAL}
done

echo "Timed out waiting for API at ${BASE_URL} after ${TIMEOUT}s"
exit 2
