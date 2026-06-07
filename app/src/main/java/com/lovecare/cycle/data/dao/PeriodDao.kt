package com.lovecare.cycle.data.dao

import androidx.room.*
import com.lovecare.cycle.data.entity.PeriodRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Query("SELECT * FROM period_records ORDER BY startDate DESC")
    fun getAllPeriods(): Flow<List<PeriodRecord>>

    @Query("SELECT * FROM period_records WHERE id = :id")
    suspend fun getPeriodById(id: Long): PeriodRecord?

    @Query("SELECT * FROM period_records ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestPeriod(): PeriodRecord?

    @Query("SELECT * FROM period_records ORDER BY startDate DESC LIMIT :limit")
    suspend fun getRecentPeriods(limit: Int): List<PeriodRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodRecord): Long

    @Update
    suspend fun updatePeriod(period: PeriodRecord)

    @Delete
    suspend fun deletePeriod(period: PeriodRecord)

    @Query("DELETE FROM period_records WHERE id = :id")
    suspend fun deletePeriodById(id: Long)

    @Query("SELECT COUNT(*) FROM period_records")
    suspend fun getPeriodCount(): Int
}
