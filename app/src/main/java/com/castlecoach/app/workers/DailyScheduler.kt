package com.castlecoach.app.workers

import android.content.Context
import androidx.work.*
import com.castlecoach.app.data.ItemType
import com.castlecoach.app.data.PlanFactory
import java.util.concurrent.TimeUnit

class DailyScheduler(private val context: Context) {

    fun scheduleForToday() {
        val items = PlanFactory.buildFor(java.time.LocalDate.now())

        items.forEach { item ->
            val delayMinutes = java.time.Duration.between(
                java.time.LocalDateTime.now(),
                item.at
            ).toMinutes().coerceAtLeast(0)

            val data = workDataOf(
                "title" to item.title,
                "type" to item.type.name,
                "details" to item.details
            )

            // NOTE: use the TimeUnit overload to avoid the Duration type mismatch you saw.
            val req = OneTimeWorkRequestBuilder<NotifyWorker>()
                .setInputData(data)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "plan-${item.at}-${item.type}-${item.title}",
                ExistingWorkPolicy.REPLACE,
                req
            )
        }
    }
}

class NotifyWorker(appContext: Context, params: WorkerParameters) :
    Worker(appContext, params) {
    override fun doWork(): Result {
        // TODO: Build and show a notification with NotificationCompat.
        // (Your manifest and channels are already set up.)
        return Result.success()
    }
}
