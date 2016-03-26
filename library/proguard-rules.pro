# Keep generated class names because it is loaded with `Class.forName()` in StaticGsonTypeAdapterFactory
-keep @com.github.gfx.static_gson.annotation.StaticGsonGenerated class * { *; }
