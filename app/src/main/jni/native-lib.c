#include <jni.h>

/*
 * Each function name below is the fully-qualified class that declares the matching `external fun`,
 * dots replaced by underscores, then the method name. Here that class is
 * com.yeyint.composebase.di.ConstantModule.
 *
 * Moving or renaming the package without updating these builds fine and fails at RUNTIME with
 * UnsatisfiedLinkError on the first network call. The `verifyJniSymbols` Gradle task (wired into
 * preBuild) now fails the build on a mismatch — see app/build.gradle.kts.
 */

JNIEXPORT jstring JNICALL
Java_com_yeyint_composebase_di_ConstantModule_getBaseURL(JNIEnv *env, jobject instance) {
//production url
    return (*env)->NewStringUTF(env, "https://yourdomain/");
}

JNIEXPORT jstring JNICALL
Java_com_yeyint_composebase_di_ConstantModule_getBaseStagingURL(JNIEnv *env, jobject instance) {
    //staging url
    return (*env)->NewStringUTF(env, "https://yourdomain/");
}