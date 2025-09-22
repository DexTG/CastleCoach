package com.castlecoach.app.data


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime


val Context.dataStore by preferencesDataStore("castlecoach")


object PrefsKeys {
val START_EPOCH = longPreferencesKey("start_epoch")
val WORKOUT_ALARM_HOUR = intPreferencesKey("alarm_h")
val WORKOUT_ALARM_MIN = intPreferencesKey("alarm_m")
}


suspend fun saveStartDate(ctx: Context, date: LocalDate) {
ctx.dataStore.edit { it[PrefsKeys.START_EPOCH] = date.toEpochDay() }
}


suspend fun getStartDate(ctx: Context): LocalDate? {
val prefs = ctx.dataStore.data.first()
return prefs[PrefsKeys.START_EPOCH]?.let { LocalDate.ofEpochDay(it) }
}


suspend fun saveAlarmTime(ctx: Context, t: LocalTime) {
ctx.dataStore.edit {
it[PrefsKeys.WORKOUT_ALARM_HOUR] = t.hour
it[PrefsKeys.WORKOUT_ALARM_MIN] = t.minute
}
}


suspend fun getAlarmTime(ctx: Context): LocalTime? {
val p = ctx.dataStore.data.first()
val h = p[PrefsKeys.WORKOUT_ALARM_HOUR] ?: return null
val m = p[PrefsKeys.WORKOUT_ALARM_MIN] ?: return null
return LocalTime.of(h,m)
}
