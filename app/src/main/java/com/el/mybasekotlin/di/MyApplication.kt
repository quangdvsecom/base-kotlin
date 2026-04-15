package com.el.mybasekotlin.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDexApplication
import com.el.mybasekotlin.data.local.AppPreferences
import com.el.mybasekotlin.helpers.flowbus.FlowBusInitApplication
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by ElChuanmen on 1/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@HiltAndroidApp
class MyApplication : MultiDexApplication(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        AppPreferences.init(this)
//        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)
        FlowBusInitApplication.initializer(this)
        createNotificationChannel(this)
        createNotificationChannelDefault(this)
//        System.loadLibrary("native-lib")
    }
    private fun createNotificationChannel(context: Context) {
        val name = "Important Notifications"
        val descriptionText = "This channel is used for important notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("IMPORTANT_CHANNEL_ID", name, importance).apply {
            description = descriptionText
            setShowBadge(false)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    private fun createNotificationChannelDefault(context: Context) {
        val name = "Default Notifications"
        val descriptionText = "This channel is used for important notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("DEFAULT_CHANNEL_ID", name, importance).apply {
            description = descriptionText
            setShowBadge(false)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}