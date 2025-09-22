package com.castlecoach.app.plan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Rebuild today's schedule when device boots or app is updated
        DailyScheduler.scheduleToday(context)
    }
}
