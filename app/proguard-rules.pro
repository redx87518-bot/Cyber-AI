# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Keep Room entities
-keep class com.cyberfusion.core.database.room.entity.** { *; }

# Keep serializable models
-keep class com.cyberfusion.core.ai.models.** { *; }
-keepclassmembers class com.cyberfusion.core.ai.models.** {
    <fields>;
}

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Kotlinx serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.cyberfusion.**$$serializer { *; }
-keepclassmembers class com.cyberfusion.** {
    *** Companion;
}
-keepclasseswithmembers class com.cyberfusion.** {
    kotlinx.serialization.KSerializer serializer(...);
}
