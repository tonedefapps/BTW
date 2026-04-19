#!/bin/sh
#
# Gradle start up script for UN*X
#
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn() {
    echo "$*"
}

die() {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true ;;
  MSYS* | MINGW*) msys=true ;;
  NONSTOP*) nonstop=true ;;
esac

CLASSPATH=""
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Find java
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
    if [ ! -x "$JAVACMD" ]; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD=$(which java 2>/dev/null)
    [ -z "$JAVACMD" ] && die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" ] && [ "$darwin" = "false" ] && [ "$nonstop" = "false" ]; then
    MAX_FD_LIMIT=$(ulimit -H -n)
    if [ $? -eq 0 ] && [ "$MAX_FD_LIMIT" != "unlimited" ]; then
        if [ "$MAX_FD" = "maximum" ] || [ "$MAX_FD" = "max" ]; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n "$MAX_FD"
    fi
fi

GRADLE_OPTS="$GRADLE_OPTS \"-Dorg.gradle.appname=$APP_BASE_NAME\""

# Collect all arguments for the java command
set -- \
    -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "$@"
