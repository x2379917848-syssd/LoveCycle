package com.lovecare.cycle.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lovecare.cycle.LoveCycleApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as LoveCycleApp
            CoroutineScope(Dispatchers.IO).launch {
                val reminderEnabled = app.settingsRepository.waterReminderEnabled.first()
                if (reminderEnabled) {
                    val interval = app.settingsRepository.reminderIntervalMinutes.first()
                    ReminderScheduler(context).scheduleWaterReminder(interval)
                }
            }
        }
    }
}
