package com.lovecare.cycle.reminder

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.lovecare.cycle.LoveCycleApp
import com.lovecare.cycle.MainActivity
import com.lovecare.cycle.R
import java.util.concurrent.TimeUnit

class WaterReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "water")
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, LoveCycleApp.WATER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.reminder_water_title))
            .setContentText(applicationContext.getString(R.string.reminder_water_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "water_reminder_work"
    }
}

class ReminderScheduler(private val context: Context) {

    fun scheduleWaterReminder(intervalMinutes: Int) {
        val correctedInterval = if (intervalMinutes < 15) 15 else intervalMinutes

        val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            correctedInterval.toLong(),
            TimeUnit.MINUTES
        )
            .setInitialDelay(correctedInterval.toLong(), TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WaterReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelWaterReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(WaterReminderWorker.WORK_NAME)
    }

    fun schedulePrePeriodReminder(daysBeforePeriod: Int) {
        val workRequest = PeriodicWorkRequestBuilder<PrePeriodReminderWorker>(
            24,
            TimeUnit.HOURS
        )
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PrePeriodReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelPrePeriodReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(PrePeriodReminderWorker.WORK_NAME)
    }
}
