package com.castlecoach.app.plan

import java.time.*
import java.time.temporal.ChronoUnit

enum class ItemType { WORKOUT, SNACK, MEAL, HYDRATE, NOTE }

data class PlanItem(
    val timeMinutes: Int,              // minutes from 00:00 (e.g., 17*60 for 17:00)
    val type: ItemType,
    val title: String,
    val details: String? = null
)

fun generateTodayPlan(): List<PlanItem> = listOf(
    PlanItem(7*60,       ItemType.HYDRATE, "500 ml water"),
    PlanItem(7*60 + 30,  ItemType.MEAL,    "Breakfast", "Protein + carbs"),
    PlanItem(12*60,      ItemType.SNACK,   "Snack",     "Fruit + nuts"),
    PlanItem(17*60,      ItemType.WORKOUT, "Workout",   "Hills + dumbbells"),
    PlanItem(20*60,      ItemType.MEAL,    "Dinner",    "Lean protein + veg"),
)

internal fun todayInstantAtMinutes(m: Int, zone: ZoneId = ZoneId.systemDefault()): Instant {
    val today = LocalDate.now(zone)
    val local = today.atStartOfDay().plusMinutes(m.toLong())
    return local.atZone(zone).toInstant()
}

internal fun delayFromNowMs(target: Instant, now: Instant = Instant.now()): Long {
    val diff = ChronoUnit.MILLIS.between(now, target)
    return if (diff < 0) 0L else diff
}
