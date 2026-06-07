package com.lovecare.cycle.data.repository

import com.lovecare.cycle.data.dao.WaterDao
import com.lovecare.cycle.data.entity.WaterRecord
import kotlinx.coroutines.flow.Flow

class WaterRepository(private val waterDao: WaterDao) {
    val allWaterRecords: Flow<List<WaterRecord>> = waterDao.getAllWaterRecords()

    fun getWaterRecordsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WaterRecord>> {
        return waterDao.getWaterRecordsForDay(startOfDay, endOfDay)
    }

    fun getTotalWaterForDay(startOfDay: Long, endOfDay: Long): Flow<Int?> {
        return waterDao.getTotalWaterForDay(startOfDay, endOfDay)
    }

    suspend fun getLatestWaterRecordForDay(startOfDay: Long, endOfDay: Long): WaterRecord? {
        return waterDao.getLatestWaterRecordForDay(startOfDay, endOfDay)
    }

    suspend fun insertWaterRecord(record: WaterRecord): Long {
        return waterDao.insertWaterRecord(record)
    }

    suspend fun deleteWaterRecord(record: WaterRecord) {
        waterDao.deleteWaterRecord(record)
    }

    suspend fun deleteWaterRecordById(id: Long) {
        waterDao.deleteWaterRecordById(id)
    }

    suspend fun getRecentWaterRecordsForDay(startOfDay: Long, endOfDay: Long, limit: Int): List<WaterRecord> {
        return waterDao.getRecentWaterRecordsForDay(startOfDay, endOfDay, limit)
    }
}
