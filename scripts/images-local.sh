#!/usr/bin/env bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 \
    -Dspring.config.name=dev-local \
    -jar target/*.jar
