package com.castlecoach.app.scheduler


import android.content.Context
import androidx.work.*
import com.castlecoach.app.data.ItemType
import com.castlecoach.app.data.PlanFactory
import com.castlecoach.app.data.getStartDate
import com.castlecoach.app.workers.OneShotNotifyWorker
import java.time.*
import java.util.concurrent.TimeUnit


object DailyScheduler {
// Schedule all notifications for TODAY (meals, snacks, hydration, workout)
suspend fun scheduleToday(ctx: Context) {
val start = getStartDate(ctx) ?: LocalDate.now()
val all = PlanFactory.generate(start)
val today = LocalDate.now()


val todays = all.filter { it.whenAt.toLocalDate() == today }
todays.forEachIndexed { idx, item ->
val delayMs = Duration.between(LocalDateTime.now(), item.whenAt).toMillis()
if (delayMs > 0) {
val data = workDataOf(
"title" to when (item.type) {
ItemType.WORKOUT -> "Workout: ${item.title}"
ItemType.HYDRATION -> "Hydrate"
ItemType.MEAL -> item.title
ItemType.SNACK -> "Snack time"
},
"body" to item.body,
"channel" to when (item.type) {
ItemType.WORKOUT -> NotificationHelper.CH_WORKOUT
ItemType.HYDRATION -> NotificationHelper.CH_HYDRATION
else -> NotificationHelper.CH_MEAL
},
"id" to (item.whenAt.hour * 100 + item.whenAt.minute + idx)
)
val req = OneTimeWorkRequestBuilder<OneShotNotifyWorker>()
.setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
.setInputData(data)
.build()
WorkManager.getInstance(ctx).enqueue(req)
}
}
}


// Schedule a daily job that re-runs each morning at 05:00 to plan the day
fun ensureDailyKickoff(ctx: Context) {
val req = PeriodicWorkRequestBuilder<DailyKickWorker>(1, TimeUnit.DAYS)
.setInitialDelay(initialDelayTo(LocalTime.of(5,0)))
.build()
WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
"daily_kickoff",
ExistingPeriodicWorkPolicy.UPDATE,
req
)
}


private fun initialDelayTo(t: LocalTime): Long {
val now = LocalDateTime.now()
var target = LocalDateTime.of(LocalDate.now(), t)
if (target.isBefore(now)) target = target.plusDays(1)
return Duration.between(now, target).toMillis()
}
}


class DailyKickWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
override suspend fun doWork(): Result {
DailyScheduler.scheduleToday(applicationContext)
return Result.success()
}
}
