-keepattributes SourceFile,LineNumberTable,Signature

# for StaticGson
-keepnames @com.github.gfx.static_gson.annotation.StaticGsonGenerated class *

# for others
-dontwarn okio.**

-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
