# Add project specific ProGuard rules here.
# Keep Room entities and Gson-serialized models from being stripped/renamed.
-keep class com.lifelink.data.local.entity.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
