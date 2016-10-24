#!/bin/bash

if [[ ${TRAVIS_BRANCH} == 'master' || ${TRAVIS_BRANCH} == 'develop' || ${TRAVIS_BRANCH} == 'support/0.27' ]]
then
  echo "Deploying Jar to Maven Central"
  mvn deploy -DskipTests --settings settings.xml -Prelease
else
  echo "Not deploying jar"
fi
