package com.castlecoach.app.data
// Daily meals/snacks/hydration scaffold for 14 days
repeat(14) { d ->
items += listOf(
meal(d.toLong(), 8, "Breakfast"),
snack(d.toLong(), 10),
meal(d.toLong(), 12, "Lunch"),
snack(d.toLong(), 16),
meal(d.toLong(), 19, "Dinner"),
hydrate(d.toLong(), 9), hydrate(d.toLong(), 11), hydrate(d.toLong(), 13), hydrate(d.toLong(), 15), hydrate(d.toLong(), 17)
)
}


return items.sortedBy { it.whenAt }
}


// --- day builders ---
private fun workoutDay(d: Long, start: LocalDate, time: ()->LocalDateTime): List<PlanItem> = listOf(
PlanItem(time(), ItemType.WORKOUT, "Hills + Walk Intervals",
"10 min warmup walk → 6–8 hill repeats (power up, easy down) → 5.5 km steady walk.")
)
private fun strengthUpperDay(d: Long, start: LocalDate): List<PlanItem> = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(18,0)), ItemType.WORKOUT,
"Strength: Upper + Core",
"DB bench 3x10–12, 1‑arm row 3x10–12/side, shoulder press 3x10, farmer’s carry x4, plank 3x30–60s")
)
private fun steadyWalkDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(9,0)), ItemType.WORKOUT,
"Walk + 2–3 jog intervals",
"5.5 km brisk walk. Add 2–3 × (30–60 s jog, walk to recover)")
)
private fun strengthLowerDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(18,0)), ItemType.WORKOUT,
"Strength: Lower + Core",
"Goblet squat 3x12, step‑ups 3x10/leg, RDL 3x12, side plank 3x30s/side")
)
private fun hillPowerDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(9,0)), ItemType.WORKOUT,
"Hill Power + Core",
"10 min warmup → 8–10 hill climbs (fast up, easy down) → 3x15 crunches")
)
private fun recoveryDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(10,0)), ItemType.WORKOUT,
"Active recovery",
"Easy 3–4 km walk + stretching/mobility")
)
private fun longHikeSimDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(9,30)), ItemType.WORKOUT,
"Long hike (simulation)",
"6–7 km varied pace. Every km: 10 push‑ups on bench, 20 squats, 20 s hang or DB hold.")
)
private fun strengthMixDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(18,0)), ItemType.WORKOUT,
"Strength circuit (light)",
"3 rounds: 10 push‑ups, 12 rows/arm, 15 squats, 20 s plank")
)
private fun hillShortDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(9,0)), ItemType.WORKOUT,
"Short hill intervals",
"6 × hill climbs (fast up) + 3 km steady walk finish")
)
private fun steadyWalkIntervalsDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(9,0)), ItemType.WORKOUT,
"5.5 km with 3–4 jog intervals",
"Jog 30–45 s then walk. Keep it comfortable.")
)
private fun strengthUpperLightDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(18,0)), ItemType.WORKOUT,
"Upper (light) + carry",
"Bench 3x8, rows 3x8/arm, shoulder press 3x8, farmer’s carry 3x30 steps")
)
private fun lightMobilityDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(10,0)), ItemType.WORKOUT,
"Short walk + mobility",
"3–4 km easy. Stretch calves, hamstrings, hips.")
)
private fun simulationDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(9,30)), ItemType.WORKOUT,
"Simulation walk",
"4–5 km. Every km: 10 push‑ups, 15 squats, 20 s hang or DB hold.")
)
private fun restDay(d: Long, start: LocalDate) = listOf(
PlanItem(LocalDateTime.of(start.plusDays(d), LocalTime.of(10,0)), ItemType.WORKOUT,
"Rest / light walk",
"2–3 km easy, hydrate, stretch, prep for race.")
)
}
