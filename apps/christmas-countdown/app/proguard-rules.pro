# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Room (pulled in by WorkManager, which Glance uses for the widget) creates its generated
# *_Impl databases by reflection. Room's own rule keeps the class but not its constructor,
# which R8's strict full mode (default since AGP 9) then removes, crashing the app on start.
-keep class * extends androidx.room.RoomDatabase {
    <init>();
}
