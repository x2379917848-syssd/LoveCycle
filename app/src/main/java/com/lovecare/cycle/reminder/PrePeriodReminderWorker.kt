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
import com.lovecare.cycle.data.entity.PeriodRecord
import com.lovecare.cycle.util.PeriodPredictionUtil
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class PrePeriodReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val app = applicationContext as LoveCycleApp
        val prePeriodDays = runCatching {
            app.settingsRepository.prePeriodReminderDays.first()
        }.getOrDefault(2)

        val prediction = runCatching {
            val periods = app.periodRepository.getRecentPeriods(6)
            val defaultCycle = runCatching { app.settingsRepository.periodDefaultCycleLength.first() }.getOrDefault(28)
            PeriodPredictionUtil.calculatePrediction(periods, defaultCycle)
        }.getOrNull()

        if (prediction?.daysUntilNextPeriod != null) {
            if (prediction.daysUntilNextPeriod in 0..prePeriodDays) {
                showNotification()
            }
        }

        return Result.success()
    }

    private fun showNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, LoveCycleApp.PERIOD_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.reminder_period_title))
            .setContentText(applicationContext.getString(R.string.reminder_period_content))
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
        const val NOTIFICATION_ID = 1002
        const val WORK_NAME = "pre_period_reminder_work"
    }
}
