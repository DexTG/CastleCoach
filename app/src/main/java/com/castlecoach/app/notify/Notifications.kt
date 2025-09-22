package com.castlecoach.app.notify

import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.castlecoach.app.R

const val PLAN_CHANNEL_ID = "plan_notifications"

fun ensurePlanChannel(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(PLAN_CHANNEL_ID) == null) {
            val ch = NotificationChannel(
                PLAN_CHANNEL_ID,
                "Daily plan",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            ch.description = "Reminders for workouts, meals, snacks and hydration"
            nm.createNotificationChannel(ch)
        }
    }
}

fun buildPlanNotification(ctx: Context, title: String, text: String? = null): Notification {
    return NotificationCompat.Builder(ctx, PLAN_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)   // add a tiny vector (24dp) named ic_notification in drawable
        .setContentTitle(title)
        .setContentText(text ?: "")
        .setStyle(NotificationCompat.BigTextStyle().bigText(text ?: ""))
        .setAutoCancel(true)
        .build()
}
