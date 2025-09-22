package com.castlecoach.app.notify

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotifyWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.success()
        val text  = inputData.getString("text")

        // Prefer an explicit id from the enqueuer, else make a stable-ish fallback
        val notificationId = inputData.getInt(
            "notificationId",
            // fallback: positive 31-bit int from the worker UUID
            id.hashCode() and 0x7FFFFFFF
            // or: (System.currentTimeMillis() and 0x7FFFFFFF).toInt() for always-unique
        )

        ensurePlanChannel(applicationContext)
        val n = buildPlanNotification(applicationContext, title, text)

        NotificationManagerCompat.from(applicationContext).notify(notificationId, n)
        return Result.success()
    }
}
