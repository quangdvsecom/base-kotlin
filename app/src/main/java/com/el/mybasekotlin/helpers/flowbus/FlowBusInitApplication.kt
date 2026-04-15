package com.el.mybasekotlin.helpers.flowbus

import android.app.Application
import androidx.multidex.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
object FlowBusInitApplication {
    lateinit var application: Application

    fun initializer(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        FlowBusInitApplication.application = application
    }
}