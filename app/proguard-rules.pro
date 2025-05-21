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

# ----- Print Informations -----

-printseeds release/seeds.txt
-printusage release/usage.txt
-printmapping release/mapping.txt
-printconfiguration release/configuration.txt

# ----- CHelper Native Core -----

-keep class yancey.chelper.core.Suggestion{ *; }
-keep class yancey.chelper.core.Theme{ *; }
-keep class yancey.chelper.core.ErrorReason{ *; }
-keep class yancey.chelper.core.ClickSuggestionResult{ *; }

# -----Command Lab -----

-keep class yancey.chelper.network.library.data.**{ *; }
-keep class yancey.chelper.network.library.service.**{ *; }

# ----- end -----
