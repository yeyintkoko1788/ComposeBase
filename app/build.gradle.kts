import java.io.FileInputStream
import java.util.Properties

// Release signing keys live in local.properties (gitignored), never in this file or the repo:
//   RELEASE_STORE_FILE=/absolute/path/to/release.jks
//   RELEASE_STORE_PASSWORD=...
//   RELEASE_KEY_ALIAS=...
//   RELEASE_KEY_PASSWORD=...
// Loaded eagerly so a fresh checkout without them can still build debug/staging, and a release
// build without them degrades to an UNSIGNED artifact with a warning (below) rather than failing
// configuration for everyone who isn't cutting a release.
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { load(it) }
    }
}
val hasReleaseSigning = listOf(
    "RELEASE_STORE_FILE", "RELEASE_STORE_PASSWORD", "RELEASE_KEY_ALIAS", "RELEASE_KEY_PASSWORD"
).all { !localProperties.getProperty(it).isNullOrBlank() }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.navigation.safeargs)
    // Note: applied via the Kotlin Gradle plugin's own version (like One_Check does) rather
    // than `libs.plugins.kotlin.serialization`, whose version.ref in the catalog points at the
    // kotlinx-serialization *runtime library* version (1.7.0), not a valid compiler plugin
    // version — using that alias would fail to resolve.
    kotlin("plugin.serialization")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.yeyint.composebase"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.composebase"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    ndkVersion = "28.1.13356709"
    flavorDimensions += "environment"
    productFlavors {
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging" // Adds ".staging" to the package name
            //versionNameSuffix = "-staging"  // Optional: Adds "-staging" to the version name
            buildConfigField("boolean", "IS_PRODUCTION", "false")
            resValue("string", "app_name", "Base Staging") // Optional: Custom app name
        }
        create("production") {
            dimension = "environment"
            // No suffix for production (uses the default package name)
            buildConfigField("boolean", "IS_PRODUCTION", "true")
            resValue("string", "app_name", "Base Production") // Optional: Custom app name
        }
    }
    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(localProperties.getProperty("RELEASE_STORE_FILE"))
                storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD")
                keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS")
                keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            // R8 is on from the start. Turning it on late is how a project ships its first
            // minified build blind: every reflection/JNI/Gson assumption surfaces at once, in a
            // release-only crash. proguard-rules.pro carries the rules this scaffold's stack needs
            // — add a keep alongside any new reflection-based library, and re-run
            // `assembleProductionRelease` after dependency changes.
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            } else if (gradle.startParameter.taskNames.any { it.contains("release", ignoreCase = true) }) {
                // Only when a release task was actually requested — not on every sync.
                logger.warn("RELEASE_* signing keys missing from local.properties — this release build will be UNSIGNED.")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // The in-app language picker switches locale independently of the device's system language.
    // With the AAB default (language splits on), Play only installs the values-<lang> resources
    // for languages in the device's system language list, so picking Burmese/Chinese in-app on a
    // device set to English silently falls back to the default strings — while a directly
    // installed APK (which carries every language) works fine. All languages must ship in the
    // base split.
    bundle {
        language {
            enableSplit = false
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.android.lottie.compose)
    implementation(libs.compose.material.icons)
    implementation(libs.coil.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.security.crypto)
//    implementation(libs.koin.compose.viewmodel)
//    implementation(libs.koin.navigation3)

    //nav2
    implementation(libs.android.navigation.compose)
    //nav3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.github.chucker.debug)
    releaseImplementation(libs.github.chucker.no.op)

    //retrofit
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.jsoup)

    //flow
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.retrofit2.kotlin.coroutines.adapter)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.resultat)

    //room
    implementation(libs.room.rumtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
//    implementation(libs.androidx.hilt.common)
//    implementation (libs.androidx.hilt.work)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.kizitonwose.calendar)
}

// ── JNI symbol check ────────────────────────────────────────────────────────────────────────────
// A JNI function name encodes the fully-qualified class that declares the matching `external fun`.
// Rename or move that class's package without renaming the C function and the build still
// succeeds — then dies with UnsatisfiedLinkError on the first network call, which reads as a
// networking bug. This fails the build instead, and also checks proguard-rules.pro still names the
// class (R8 renaming it breaks the binding the same way, but only in release).
val verifyJniSymbols by tasks.registering {
    group = "verification"
    description = "Fails if native C symbols don't match the Kotlin classes declaring external funs."

    val nativeSource = layout.projectDirectory.file("src/main/jni/native-lib.c")
    val proguardRules = layout.projectDirectory.file("proguard-rules.pro")
    val kotlinSources = fileTree(layout.projectDirectory.dir("src/main/java")) { include("**/*.kt") }
    inputs.file(nativeSource)
    inputs.file(proguardRules)
    inputs.files(kotlinSources)

    doLast {
        // JNI name mangling: '_' in an identifier becomes "_1", then '.' separators become '_'.
        fun mangle(name: String) = name.replace("_", "_1").replace(".", "_")

        val cSymbols = Regex("""Java_(\w+)\s*\(""")
            .findAll(nativeSource.asFile.readText())
            .map { it.groupValues[1] }
            .toSet()

        val expected = mutableMapOf<String, String>() // mangled symbol (sans "Java_") -> description
        val classesNeedingKeep = mutableSetOf<String>()
        kotlinSources.files.forEach { file ->
            val text = file.readText()
            val externals = Regex("""\bexternal\s+fun\s+(\w+)""").findAll(text).map { it.groupValues[1] }.toList()
            if (externals.isEmpty()) return@forEach
            val pkg = Regex("""(?m)^package\s+([\w.]+)""").find(text)?.groupValues?.get(1)
            val cls = Regex("""\b(?:class|object)\s+(\w+)""").find(text)?.groupValues?.get(1)
            if (pkg == null || cls == null) {
                throw GradleException("verifyJniSymbols: cannot read package/class from ${file.name}")
            }
            val fqn = "$pkg.$cls"
            classesNeedingKeep += fqn
            externals.forEach { fn -> expected["${mangle(fqn)}_${mangle(fn)}"] = "$fqn.$fn" }
        }

        val problems = mutableListOf<String>()
        expected.forEach { (symbol, what) ->
            if (symbol !in cSymbols) problems += "missing in native-lib.c: Java_$symbol  (for $what)"
        }
        (cSymbols - expected.keys).forEach { problems += "native-lib.c defines Java_$it, but no Kotlin `external fun` maps to it (stale package name?)" }
        val rules = proguardRules.asFile.readText()
        classesNeedingKeep.filter { it !in rules }.forEach {
            problems += "proguard-rules.pro does not mention $it — R8 may rename it and break the native binding"
        }

        if (problems.isNotEmpty()) {
            throw GradleException(
                "JNI symbol mismatch — this builds fine and fails at runtime with UnsatisfiedLinkError:\n" +
                    problems.joinToString("\n") { "  - $it" } +
                    "\nFix app/src/main/jni/native-lib.c (and the keep rule in proguard-rules.pro) to match the class's package."
            )
        }
        logger.lifecycle("verifyJniSymbols: ${expected.size} JNI symbol(s) OK")
    }
}

tasks.named("preBuild") { dependsOn(verifyJniSymbols) }