package com.castlecoach.app.scheduler


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BootReceiver : BroadcastReceiver() {
override fun onReceive(context: Context, intent: Intent) {
if (intent.action?.contains("BOOT_COMPLETED") == true) {
DailyScheduler.ensureDailyKickoff(context)
CoroutineScope(Dispatchers.Default).launch {
DailyScheduler.scheduleToday(context)
}
}
}
}
