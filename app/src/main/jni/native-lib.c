#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_composebase_di_ConstantModule_getBaseURL(JNIEnv *env, jobject instance) {
//production url
    return (*env)->NewStringUTF(env, "https://yourdomain/");
}

JNIEXPORT jstring JNICALL
Java_com_example_composebase_di_ConstantModule_getBaseStagingURL(JNIEnv *env, jobject instance) {
    //staging url
    return (*env)->NewStringUTF(env, "https://yourdomain/");
}