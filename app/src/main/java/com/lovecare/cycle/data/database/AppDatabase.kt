package com.lovecare.cycle.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lovecare.cycle.data.dao.AnniversaryDao
import com.lovecare.cycle.data.dao.DiaryDao
import com.lovecare.cycle.data.dao.PeriodDao
import com.lovecare.cycle.data.dao.WaterDao
import com.lovecare.cycle.data.entity.Anniversary
import com.lovecare.cycle.data.entity.DiaryEntry
import com.lovecare.cycle.data.entity.PeriodRecord
import com.lovecare.cycle.data.entity.WaterRecord

@Database(
    entities = [
        PeriodRecord::class,
        WaterRecord::class,
        DiaryEntry::class,
        Anniversary::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao
    abstract fun waterDao(): WaterDao
    abstract fun diaryDao(): DiaryDao
    abstract fun anniversaryDao(): AnniversaryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lovecycle_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
