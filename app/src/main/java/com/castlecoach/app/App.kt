package com.castlecoach.app


import android.app.Application
import com.castlecoach.app.scheduler.NotificationHelper


class App : Application() {
override fun onCreate() {
super.onCreate()
NotificationHelper.createChannels(this)
}
}
