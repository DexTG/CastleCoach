package com.castlecoach.app.data

import java.time.*
import com.castlecoach.app.data.ItemType


data class PlanItem(
    val at: LocalDateTime,
    val type: ItemType,
    val title: String,
    val details: String = ""
)

object PlanFactory {
    /**
     * Very simple day template you can evolve later.
     * Times are in local device time.
     */
    fun buildFor(date: LocalDate): List<PlanItem> {
        fun t(h: Int, m: Int = 0) = LocalDateTime.of(date, LocalTime.of(h, m))

        return listOf(
            PlanItem(t(7),  ItemType.HYDRATE, "500 ml water"),
            PlanItem(t(7,30), ItemType.MEAL, "Breakfast", "Protein + carbs + fruit"),
            PlanItem(t(10), ItemType.SNACK, "Snack", "Greek yogurt or nuts + fruit"),
            PlanItem(t(12,30), ItemType.MEAL, "Lunch", "Protein bowl / wrap"),
            PlanItem(t(15), ItemType.HYDRATE, "500 ml water"),
            PlanItem(t(16), ItemType.WALK, "Hill repeats x6", "Short and fast"),
            PlanItem(t(18,30), ItemType.MEAL, "Dinner", "Lean protein + veg + carbs"),
            PlanItem(t(20), ItemType.SNACK, "Snack (optional)", "Cottage cheese + berries"),
            PlanItem(t(21), ItemType.HYDRATE, "300 ml water")
        )
    }
}
