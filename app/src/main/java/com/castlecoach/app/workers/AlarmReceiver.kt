package com.castlecoach.app.scheduler


import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.castlecoach.app.R


class AlarmReceiver : BroadcastReceiver() {
override fun onReceive(context: Context, intent: Intent) {
val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
val notif = NotificationCompat.Builder(context, NotificationHelper.CH_WORKOUT)
.setSmallIcon(R.drawable.ic_notification)
.setContentTitle("Workout time")
.setContentText(intent.getStringExtra("msg") ?: "Let’s go!")
.setAutoCancel(true)
.build()
nm.notify((SystemClock.elapsedRealtime()%Int.MAX_VALUE).toInt(), notif)
}
}


fun scheduleWorkoutAlarm(ctx: Context, hour: Int, min: Int) {
val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
val intent = Intent(ctx, AlarmReceiver::class.java).apply { putExtra("msg", "Time to train — you got this") }
val pi = PendingIntent.getBroadcast(ctx, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


val now = java.time.LocalDateTime.now()
var trigger = java.time.LocalDateTime.of(java.time.LocalDate.now(), java.time.LocalTime.of(hour, min))
if (trigger.isBefore(now)) trigger = trigger.plusDays(1)
val millis = trigger.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()


// Use AlarmClock to avoid exact alarm permission.
val ac = AlarmManager.AlarmClockInfo(millis, pi)
am.setAlarmClock(ac, pi)
}
