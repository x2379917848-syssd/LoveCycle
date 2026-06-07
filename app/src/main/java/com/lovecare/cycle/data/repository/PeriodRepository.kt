package com.lovecare.cycle.data.repository

import com.lovecare.cycle.data.dao.PeriodDao
import com.lovecare.cycle.data.entity.PeriodRecord
import kotlinx.coroutines.flow.Flow

class PeriodRepository(private val periodDao: PeriodDao) {
    val allPeriods: Flow<List<PeriodRecord>> = periodDao.getAllPeriods()

    suspend fun getPeriodById(id: Long): PeriodRecord? {
        return periodDao.getPeriodById(id)
    }

    suspend fun getLatestPeriod(): PeriodRecord? {
        return periodDao.getLatestPeriod()
    }

    suspend fun getRecentPeriods(limit: Int): List<PeriodRecord> {
        return periodDao.getRecentPeriods(limit)
    }

    suspend fun insertPeriod(period: PeriodRecord): Long {
        return periodDao.insertPeriod(period)
    }

    suspend fun updatePeriod(period: PeriodRecord) {
        periodDao.updatePeriod(period.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deletePeriod(period: PeriodRecord) {
        periodDao.deletePeriod(period)
    }

    suspend fun deletePeriodById(id: Long) {
        periodDao.deletePeriodById(id)
    }

    suspend fun getPeriodCount(): Int {
        return periodDao.getPeriodCount()
    }
}
