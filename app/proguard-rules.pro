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

# ----- Print informations -----

-printseeds release/seeds.txt
-printusage release/usage.txt
-printmapping release/mapping.txt
-printconfiguration release/configuration.txt

# ----- CHelper Native Core -----

-keep class yancey.chelper.core.Suggestion{ *; }
-keep class yancey.chelper.core.Theme{ *; }
-keep class yancey.chelper.core.ErrorReason{ *; }
-keep class yancey.chelper.core.ClickSuggestionResult{ *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# ----- OpenLans's Command Library -----

-keep class yancey.chelper.network.data.openlans.**{ *; }

# ----- end -----
