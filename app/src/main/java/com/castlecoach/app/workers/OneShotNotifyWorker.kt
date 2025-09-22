package com.castlecoach.app.workers


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.castlecoach.app.scheduler.NotificationHelper
import kotlin.random.Random


class OneShotNotifyWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
override suspend fun doWork(): Result {
val title = inputData.getString("title") ?: return Result.failure()
val body = inputData.getString("body") ?: ""
val channel = inputData.getString("channel") ?: NotificationHelper.CH_MEAL
val id = inputData.getInt("id", Random.nextInt())


val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
val notif = NotificationHelper.build(applicationContext, channel, title, body, id)
nm.notify(id, notif)
return Result.success()
}
}
