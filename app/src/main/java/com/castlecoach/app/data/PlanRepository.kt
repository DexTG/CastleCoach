package com.castlecoach.app.data

import android.content.Context
import androidx.room.Room
import kotlinx.datetime.*

class PlanRepository private constructor(context: Context) {
    private val db = Room.databaseBuilder(context, AppDb::class.java, "castlecoach.db").build()
    private val dao = db.planDao()

    suspend fun getToday(): List<PlanItemWithDone> = dao.getForDate(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    suspend fun markDone(id: Long) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        dao.upsertCompletion(CompletionEntity(planItemId = id, completedAt = now))
    }

    suspend fun markUndone(id: Long) = dao.clearCompletion(id)

    suspend fun ensureTodayPlanGenerated() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val existing = dao.getForDate(today)
        if (existing.isNotEmpty()) return

        // Simple default template – tweak times and contents as you like
        val items = listOf(
            PlanItemEntity(date = today, timeMinutes = 7*60, type = ItemType.HYDRATE, title = "500 ml water"),
            PlanItemEntity(date = today, timeMinutes = 7*60+30, type = ItemType.MEAL, title = "Breakfast", details = "Protein + carbs"),
            PlanItemEntity(date = today, timeMinutes = 12*60, type = ItemType.SNACK, title = "Snack", details = "Fruit + nuts"),
            PlanItemEntity(date = today, timeMinutes = 17*60, type = ItemType.WORKOUT, title = "CastleCoach workout", details = "Hills + dumbbells"),
            PlanItemEntity(date = today, timeMinutes = 20*60, type = ItemType.MEAL, title = "Dinner", details = "Lean protein + veg"),
        )
        dao.insertAll(items)
    }

    companion object {
        @Volatile private var instance: PlanRepository? = null
        fun get(context: Context): PlanRepository =
            instance ?: synchronized(this) { instance ?: PlanRepository(context.applicationContext).also { instance = it } }
    }
}
