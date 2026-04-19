#!/bin/sh
# Quick build + install to connected device
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew installDebug && \
~/Library/Android/sdk/platform-tools/adb shell am start -n com.btw.app/.MainActivity
