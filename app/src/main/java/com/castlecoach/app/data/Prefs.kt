package com.castlecoach.app.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.userPrefs by preferencesDataStore("user_prefs")

object PrefKeys {
    val HYDRATION_GOAL = intPreferencesKey("hydration_goal_ml")
    val HYDRATION_TODAY = intPreferencesKey("hydration_today_ml")
    val STOPWATCH_BASE = longPreferencesKey("stopwatch_base_ms") // if you want to persist
}

suspend fun setInt(context: Context, key: Preferences.Key<Int>, value: Int) {
    context.userPrefs.edit { it[key] = value }
}
suspend fun getInt(context: Context, key: Preferences.Key<Int>, def: Int = 0): Int {
    val p = context.userPrefs.data.first()
    return p[key] ?: def
}
suspend fun setLong(context: Context, key: Preferences.Key<Long>, value: Long) {
    context.userPrefs.edit { it[key] = value }
}
suspend fun getLong(context: Context, key: Preferences.Key<Long>, def: Long = 0L): Long {
    val p = context.userPrefs.data.first()
    return p[key] ?: def
}
