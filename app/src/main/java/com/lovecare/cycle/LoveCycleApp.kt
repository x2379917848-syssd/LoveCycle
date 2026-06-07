package com.lovecare.cycle

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.lovecare.cycle.data.database.AppDatabase
import com.lovecare.cycle.data.repository.SettingsRepository
import com.lovecare.cycle.data.repository.PeriodRepository
import com.lovecare.cycle.data.repository.WaterRepository
import com.lovecare.cycle.data.repository.DiaryRepository
import com.lovecare.cycle.data.repository.AnniversaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class LoveCycleApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(this) }
    val periodRepository: PeriodRepository by lazy { PeriodRepository(database.periodDao()) }
    val waterRepository: WaterRepository by lazy { WaterRepository(database.waterDao()) }
    val diaryRepository: DiaryRepository by lazy { DiaryRepository(database.diaryDao()) }
    val anniversaryRepository: AnniversaryRepository by lazy { AnniversaryRepository(database.anniversaryDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val waterChannel = NotificationChannel(
                WATER_CHANNEL_ID,
                "喝水提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "提醒您按时喝水"
                enableVibration(true)
            }

            val periodChannel = NotificationChannel(
                PERIOD_CHANNEL_ID,
                "经期提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "提醒您经期即将到来"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(waterChannel)
            notificationManager.createNotificationChannel(periodChannel)
        }
    }

    companion object {
        const val WATER_CHANNEL_ID = "water_reminder"
        const val PERIOD_CHANNEL_ID = "period_reminder"
    }
}
