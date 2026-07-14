#!/bin/sh

#
# Gradle start up script for POSIX. Standard Gradle wrapper launcher script.
# NOTE: gradle-wrapper.jar is not bundled in this scaffold (see README "Getting started").
# Android Studio will regenerate it on first project sync, or run `gradle wrapper` once
# with any local Gradle 8.6+ installation to generate it yourself.
#

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
APP_NAME="Gradle"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if ! [ -f "$CLASSPATH" ]; then
  echo "gradle-wrapper.jar not found at $CLASSPATH."
  echo "Open this project in Android Studio and let it regenerate the wrapper,"
  echo "or run: gradle wrapper --gradle-version 8.6"
  exit 1
fi

JAVACMD="java"
if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
