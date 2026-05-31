# SQLCipher native library
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Standard — required by Hilt, Room, WorkManager, and Kotlin reflection
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
