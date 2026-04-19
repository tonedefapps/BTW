-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
