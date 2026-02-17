package com.yeyint.composebase

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseComposeApp : Application(){

    companion object {
        lateinit var instance: BaseComposeApp
            private set

        fun getContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}