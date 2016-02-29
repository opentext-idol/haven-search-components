#!/bin/bash


echo "Deploying Jar to Maven Central"
mvn deploy -DskipTests --settings settings.xml -Prelease

