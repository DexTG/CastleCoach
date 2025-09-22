package com.castlecoach.app.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationManagerCompat

class NotifyWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.success()
        val text  = inputData.getString("text")
        ensurePlanChannel(applicationContext)
        val n = buildPlanNotification(applicationContext, title, text)
        NotificationManagerCompat.from(applicationContext).notify(id, n)
        return Result.success()
    }
}
