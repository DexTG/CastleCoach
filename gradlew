#!/usr/bin/env sh
APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME"; pwd)
exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
