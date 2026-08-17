# Сохраняем все Entity для Firestore
-keep class com.fameli.budget.data.local.entity.** { *; }
-keepclassmembers class com.fameli.budget.data.local.entity.** {
    <init>();
    *** get*();
    *** set*();
}

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Kotlinx Serialization
-keepattributes *Annotation*, Signature
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
