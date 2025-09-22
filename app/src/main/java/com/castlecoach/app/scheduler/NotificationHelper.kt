package com.castlecoach.app.scheduler


import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.castlecoach.app.R


object NotificationHelper {
const val CH_WORKOUT = "workout"
const val CH_HYDRATION = "hydration"
const val CH_MEAL = "meal"


fun createChannels(ctx: Context) {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
nm.createNotificationChannel(NotificationChannel(CH_WORKOUT, "Workouts", NotificationManager.IMPORTANCE_HIGH))
nm.createNotificationChannel(NotificationChannel(CH_HYDRATION, "Hydration", NotificationManager.IMPORTANCE_DEFAULT))
nm.createNotificationChannel(NotificationChannel(CH_MEAL, "Meals & Snacks", NotificationManager.IMPORTANCE_DEFAULT))
}
}


fun build(ctx: Context, channel: String, title: String, body: String, id: Int): Notification {
val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)?.apply {
addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
val pi = PendingIntent.getActivity(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
return NotificationCompat.Builder(ctx, channel)
.setSmallIcon(R.drawable.ic_notification)
.setContentTitle(title)
.setContentText(body)
.setStyle(NotificationCompat.BigTextStyle().bigText(body))
.setAutoCancel(true)
.setContentIntent(pi)
.build()
}
}
