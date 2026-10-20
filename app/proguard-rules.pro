# R8 rules for the ComposeBase scaffold. Minification is ON for release builds, so every rule here
# exists because the stack below needs it — when you add a library that uses reflection, JNI,
# or serialization by field name, add its keep here and run `./gradlew assembleProductionRelease`
# (a debug build proves nothing about R8). When cloning, the package in the JNI and serialization
# rules below must follow the rename — `./gradlew verifyJniSymbols` checks the JNI one.

# ──────────────────────────────────────────────
# General
# ──────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod

# Repackage all obfuscated classes into the root package to further compact the dex file (this
# becomes AGP's default starting 9.1, but can be opted into now as a standard R8 directive).
-repackageclasses ''

# ──────────────────────────────────────────────
# Native (JNI) — ConstantModule.getBaseURL()/getBaseStagingURL()
# A native method binds to the Java/Kotlin side by the exact class+method name the .so was built
# against. If R8 renames ConstantModule (or its methods) the library can no longer find them and
# every call throws UnsatisfiedLinkError at runtime, not at compile time. The same failure happens
# if the package is renamed without updating native-lib.c — see verifyJniSymbols in
# app/build.gradle.kts, which also requires this rule to still name the class.
# ──────────────────────────────────────────────
-keepclasseswithmembernames,includedescriptorclasses class com.yeyint.composebase.di.ConstantModule {
    native <methods>;
}

# ──────────────────────────────────────────────
# Kotlin
# ──────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# ──────────────────────────────────────────────
# Kotlin Coroutines — coroutines-core 1.7.0+ bundles its own consumer rules.
# ──────────────────────────────────────────────
-dontwarn kotlinx.coroutines.**

# ──────────────────────────────────────────────
# Kotlinx Serialization — used by Navigation 3's type-safe NavRoute keys (ui/navigation/NavRoute.kt),
# not for network payloads (those go through Gson below).
# ──────────────────────────────────────────────
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.yeyint.composebase.**$$serializer { *; }
-keepclassmembers class com.yeyint.composebase.** {
    *** Companion;
}
-keepclasseswithmembers class com.yeyint.composebase.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ──────────────────────────────────────────────
# Hilt / Dagger — ship their own consumer rules with exactly the keeps they need; a blanket keep
# on the whole package would disable shrinking for it.
# ──────────────────────────────────────────────
-keep class * extends dagger.hilt.android.internal.managers.ActivityComponentManager { *; }
-keepclasseswithmembernames class * {
    @dagger.* <fields>;
    @dagger.* <methods>;
    @javax.inject.* <fields>;
    @javax.inject.* <methods>;
}

# ──────────────────────────────────────────────
# Retrofit + OkHttp — retrofit 2.9.0+ bundles its own consumer rules (the @GET/@POST detection some
# templates still carry manually is redundant at this version).
# ──────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# ──────────────────────────────────────────────
# Gson — 2.11.0 bundles consumer rules for @SerializedName fields, which is the ONLY thing that
# survives renaming. The shared Gson uses LOWER_CASE_WITH_UNDERSCORES (see NetworkModule), so a
# camelCase DTO field without @SerializedName is already broken before R8; DTOs with a plain
# lower-case single-word name (meta/body/data) get away without one today only because nothing
# renames them — R8 would. Keeping the package makes the failure mode "works" rather than
# "silently null fields in release only". ResponseDtoSerializedNameTest enforces the annotations;
# narrow this keep once you trust that test.
# ──────────────────────────────────────────────
-keep class com.yeyint.composebase.network.response.** { *; }

# ──────────────────────────────────────────────
# Crash readability — keeps custom exception class names readable in a logged stack trace.
# ──────────────────────────────────────────────
-keep public class * extends java.lang.Exception

# ──────────────────────────────────────────────
# Jetpack Compose / Navigation (nav2 + Navigation 3) — ship their own consumer rules.
# ──────────────────────────────────────────────
-dontwarn androidx.compose.**
-dontwarn androidx.navigation.**
-dontwarn androidx.navigation3.**

# ──────────────────────────────────────────────
# Lottie / Coil / Jsoup / Kizitonwose Calendar
# ──────────────────────────────────────────────
-dontwarn com.airbnb.lottie.**
-dontwarn coil.**
-dontwarn org.jsoup.**
-dontwarn com.kizitonwose.calendar.**

# ──────────────────────────────────────────────
# Chucker — debug only; release uses the no-op artifact (releaseImplementation chucker-no-op).
# ──────────────────────────────────────────────
-dontwarn com.chuckerteam.chucker.**
