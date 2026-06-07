package com.lovecare.cycle.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val DAILY_WATER_GOAL_ML = intPreferencesKey("daily_water_goal_ml")
        val CUP_SIZE_ML = intPreferencesKey("cup_size_ml")
        val WATER_REMINDER_ENABLED = booleanPreferencesKey("water_reminder_enabled")
        val REMINDER_START_HOUR = intPreferencesKey("reminder_start_hour")
        val REMINDER_START_MINUTE = intPreferencesKey("reminder_start_minute")
        val REMINDER_END_HOUR = intPreferencesKey("reminder_end_hour")
        val REMINDER_END_MINUTE = intPreferencesKey("reminder_end_minute")
        val REMINDER_INTERVAL_MINUTES = intPreferencesKey("reminder_interval_minutes")
        val PERIOD_DEFAULT_CYCLE_LENGTH = intPreferencesKey("period_default_cycle_length")
        val PERIOD_DEFAULT_DURATION = intPreferencesKey("period_default_duration")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PRIVACY_LOCK_ENABLED = booleanPreferencesKey("privacy_lock_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val PRE_PERIOD_REMINDER_ENABLED = booleanPreferencesKey("pre_period_reminder_enabled")
        val PRE_PERIOD_REMINDER_DAYS = intPreferencesKey("pre_period_reminder_days")
    }

    val dailyWaterGoalMl: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DAILY_WATER_GOAL_ML] ?: 2000
    }

    val cupSizeMl: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CUP_SIZE_ML] ?: 200
    }

    val waterReminderEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[WATER_REMINDER_ENABLED] ?: false
    }

    val reminderStartHour: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_START_HOUR] ?: 9
    }

    val reminderStartMinute: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_START_MINUTE] ?: 0
    }

    val reminderEndHour: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_END_HOUR] ?: 22
    }

    val reminderEndMinute: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_END_MINUTE] ?: 0
    }

    val reminderIntervalMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_INTERVAL_MINUTES] ?: 60
    }

    val periodDefaultCycleLength: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PERIOD_DEFAULT_CYCLE_LENGTH] ?: 28
    }

    val periodDefaultDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PERIOD_DEFAULT_DURATION] ?: 5
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "system"
    }

    val privacyLockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PRIVACY_LOCK_ENABLED] ?: false
    }

    val pinHash: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PIN_HASH]
    }

    val prePeriodReminderEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PRE_PERIOD_REMINDER_ENABLED] ?: true
    }

    val prePeriodReminderDays: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PRE_PERIOD_REMINDER_DAYS] ?: 2
    }

    suspend fun setDailyWaterGoalMl(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_WATER_GOAL_ML] = value
        }
    }

    suspend fun setCupSizeMl(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[CUP_SIZE_ML] = value
        }
    }

    suspend fun setWaterReminderEnabled(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[WATER_REMINDER_ENABLED] = value
        }
    }

    suspend fun setReminderStartHour(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_START_HOUR] = value
        }
    }

    suspend fun setReminderStartMinute(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_START_MINUTE] = value
        }
    }

    suspend fun setReminderEndHour(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_END_HOUR] = value
        }
    }

    suspend fun setReminderEndMinute(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_END_MINUTE] = value
        }
    }

    suspend fun setReminderIntervalMinutes(value: Int) {
        val correctedValue = if (value < 15) 15 else value
        context.dataStore.edit { preferences ->
            preferences[REMINDER_INTERVAL_MINUTES] = correctedValue
        }
    }

    suspend fun setPeriodDefaultCycleLength(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PERIOD_DEFAULT_CYCLE_LENGTH] = value
        }
    }

    suspend fun setPeriodDefaultDuration(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PERIOD_DEFAULT_DURATION] = value
        }
    }

    suspend fun setThemeMode(value: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = value
        }
    }

    suspend fun setPrivacyLockEnabled(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PRIVACY_LOCK_ENABLED] = value
        }
    }

    suspend fun setPinHash(value: String?) {
        context.dataStore.edit { preferences ->
            if (value != null) {
                preferences[PIN_HASH] = value
            } else {
                preferences.remove(PIN_HASH)
            }
        }
    }

    suspend fun setPrePeriodReminderEnabled(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PRE_PERIOD_REMINDER_ENABLED] = value
        }
    }

    suspend fun setPrePeriodReminderDays(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PRE_PERIOD_REMINDER_DAYS] = value
        }
    }
}
