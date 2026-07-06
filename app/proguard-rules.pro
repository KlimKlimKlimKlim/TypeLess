-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keep class com.klim.typeless.domain.model.** { *; }
-keep class com.klim.typeless.data.db.SnippetEntity { *; }
-keep class com.klim.typeless.data.db.Converters { *; }
-keep class com.klim.typeless.data.db.StringListConverter { *; }

-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

-keep class androidx.room.** { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.**

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-dontwarn dagger.hilt.**

-keepnames @kotlinx.serialization.Serializable class *
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

-keep class com.klim.typeless.service.TypeLessAccessibilityService { *; }

-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

-keep class com.google.android.gms.internal.** { *; }
-dontwarn com.google.android.gms.**

-keepclassmembers class * {
    @com.google.firebase.crashlytics.internal.common.CrashlyticsUncaughtExceptionHandler *;
}