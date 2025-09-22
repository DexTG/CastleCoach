package com.castlecoach.app.plan

import android.content.Context
import androidx.work.*
import com.castlecoach.app.notify.NotifyWorker
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object DailyScheduler {
    private fun uniqueNameForToday(): String = "plan-" + java.time.LocalDate.now()

    fun scheduleToday(context: Context) {
        val plan = generateTodayPlan()
        val zone = ZoneId.systemDefault()
        val now = java.time.Instant.now()

        // Cancel and replace the day's schedule each time we call this
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(uniqueNameForToday())

        val works = plan.mapIndexed { idx, item ->
            val at = todayInstantAtMinutes(item.timeMinutes, zone)
            val delayMs = delayFromNowMs(at, now)

            val data = workDataOf(
                "title" to when (item.type) {
                    ItemType.WORKOUT -> "Workout: ${item.title}"
                    ItemType.MEAL    -> "Meal: ${item.title}"
                    ItemType.SNACK   -> "Snack: ${item.title}"
                    ItemType.HYDRATE -> "Hydration"
                    ItemType.NOTE    -> item.title
                },
                "text" to (item.details ?: "")
            )

            OneTimeWorkRequestBuilder<NotifyWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("plan-item")
                .build()
        }

        workManager.enqueueUniqueWork(
            uniqueNameForToday(),
            ExistingWorkPolicy.REPLACE,
            works
        )
    }
}
