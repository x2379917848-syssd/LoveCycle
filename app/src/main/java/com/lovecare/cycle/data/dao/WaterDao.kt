package com.lovecare.cycle.data.dao

import androidx.room.*
import com.lovecare.cycle.data.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_records ORDER BY timestamp DESC")
    fun getAllWaterRecords(): Flow<List<WaterRecord>>

    @Query("SELECT * FROM water_records WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC")
    fun getWaterRecordsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WaterRecord>>

    @Query("SELECT * FROM water_records WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWaterRecordForDay(startOfDay: Long, endOfDay: Long): WaterRecord?

    @Query("SELECT SUM(amountMl) FROM water_records WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    fun getTotalWaterForDay(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterRecord(record: WaterRecord): Long

    @Delete
    suspend fun deleteWaterRecord(record: WaterRecord)

    @Query("DELETE FROM water_records WHERE id = :id")
    suspend fun deleteWaterRecordById(id: Long)

    @Query("SELECT * FROM water_records WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentWaterRecordsForDay(startOfDay: Long, endOfDay: Long, limit: Int): List<WaterRecord>
}
