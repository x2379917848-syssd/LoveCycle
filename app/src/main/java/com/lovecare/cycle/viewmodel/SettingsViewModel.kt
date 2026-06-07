package com.lovecare.cycle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lovecare.cycle.LoveCycleApp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.MessageDigest

data class SettingsUiState(
    val dailyWaterGoalMl: Int = 2000,
    val cupSizeMl: Int = 200,
    val waterReminderEnabled: Boolean = false,
    val reminderStartHour: Int = 9,
    val reminderStartMinute: Int = 0,
    val reminderEndHour: Int = 22,
    val reminderEndMinute: Int = 0,
    val reminderIntervalMinutes: Int = 60,
    val periodDefaultCycleLength: Int = 28,
    val periodDefaultDuration: Int = 5,
    val themeMode: String = "system",
    val privacyLockEnabled: Boolean = false,
    val prePeriodReminderEnabled: Boolean = true,
    val prePeriodReminderDays: Int = 2,
    val notificationPermissionGranted: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LoveCycleApp

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            app.settingsRepository.dailyWaterGoalMl.collect { value ->
                _uiState.value = _uiState.value.copy(dailyWaterGoalMl = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.cupSizeMl.collect { value ->
                _uiState.value = _uiState.value.copy(cupSizeMl = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.waterReminderEnabled.collect { value ->
                _uiState.value = _uiState.value.copy(waterReminderEnabled = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.reminderStartHour.collect { value ->
                _uiState.value = _uiState.value.copy(reminderStartHour = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.reminderStartMinute.collect { value ->
                _uiState.value = _uiState.value.copy(reminderStartMinute = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.reminderEndHour.collect { value ->
                _uiState.value = _uiState.value.copy(reminderEndHour = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.reminderEndMinute.collect { value ->
                _uiState.value = _uiState.value.copy(reminderEndMinute = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.reminderIntervalMinutes.collect { value ->
                _uiState.value = _uiState.value.copy(reminderIntervalMinutes = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.periodDefaultCycleLength.collect { value ->
                _uiState.value = _uiState.value.copy(periodDefaultCycleLength = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.periodDefaultDuration.collect { value ->
                _uiState.value = _uiState.value.copy(periodDefaultDuration = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.themeMode.collect { value ->
                _uiState.value = _uiState.value.copy(themeMode = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.privacyLockEnabled.collect { value ->
                _uiState.value = _uiState.value.copy(privacyLockEnabled = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.prePeriodReminderEnabled.collect { value ->
                _uiState.value = _uiState.value.copy(prePeriodReminderEnabled = value)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.prePeriodReminderDays.collect { value ->
                _uiState.value = _uiState.value.copy(prePeriodReminderDays = value)
            }
        }
    }

    fun setDailyWaterGoalMl(value: Int) {
        viewModelScope.launch {
            app.settingsRepository.setDailyWaterGoalMl(value)
        }
    }

    fun setCupSizeMl(value: Int) {
        viewModelScope.launch {
            app.settingsRepository.setCupSizeMl(value)
        }
    }

    fun setWaterReminderEnabled(value: Boolean) {
        viewModelScope.launch {
            app.settingsRepository.setWaterReminderEnabled(value)
            val scheduler = ReminderScheduler(app)
            if (value) {
                val interval = _uiState.value.reminderIntervalMinutes
                scheduler.scheduleWaterReminder(interval)
            } else {
                scheduler.cancelWaterReminder()
            }
        }
    }

    fun setReminderStartTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            app.settingsRepository.setReminderStartHour(hour)
            app.settingsRepository.setReminderStartMinute(minute)
        }
    }

    fun setReminderEndTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            app.settingsRepository.setReminderEndHour(hour)
            app.settingsRepository.setReminderEndMinute(minute)
        }
    }

    fun setReminderIntervalMinutes(value: Int) {
        viewModelScope.launch {
            val corrected = if (value < 15) 15 else value
            app.settingsRepository.setReminderIntervalMinutes(corrected)
            if (_uiState.value.waterReminderEnabled) {
                ReminderScheduler(app).scheduleWaterReminder(corrected)
            }
        }
    }

    fun setPeriodDefaultCycleLength(value: Int) {
        viewModelScope.launch {
            app.settingsRepository.setPeriodDefaultCycleLength(value)
        }
    }

    fun setPeriodDefaultDuration(value: Int) {
        viewModelScope.launch {
            app.settingsRepository.setPeriodDefaultDuration(value)
        }
    }

    fun setThemeMode(value: String) {
        viewModelScope.launch {
            app.settingsRepository.setThemeMode(value)
        }
    }

    fun setPrivacyLockEnabled(value: Boolean) {
        viewModelScope.launch {
            app.settingsRepository.setPrivacyLockEnabled(value)
        }
    }

    fun setPin(pin: String) {
        viewModelScope.launch {
            val hash = hashPin(pin)
            app.settingsRepository.setPinHash(hash)
        }
    }

    fun clearPin() {
        viewModelScope.launch {
            app.settingsRepository.setPinHash(null)
        }
    }

    fun setPrePeriodReminderEnabled(value: Boolean) {
        viewModelScope.launch {
            app.settingsRepository.setPrePeriodReminderEnabled(value)
            val scheduler = ReminderScheduler(app)
            if (value) {
                scheduler.schedulePrePeriodReminder(_uiState.value.prePeriodReminderDays)
            } else {
                scheduler.cancelPrePeriodReminder()
            }
        }
    }

    fun setPrePeriodReminderDays(value: Int) {
        viewModelScope.launch {
            app.settingsRepository.setPrePeriodReminderDays(value)
        }
    }

    fun setNotificationPermissionGranted(granted: Boolean) {
        _uiState.value = _uiState.value.copy(notificationPermissionGranted = granted)
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
