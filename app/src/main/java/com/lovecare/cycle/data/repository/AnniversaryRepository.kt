package com.lovecare.cycle.data.repository

import com.lovecare.cycle.data.dao.AnniversaryDao
import com.lovecare.cycle.data.entity.Anniversary
import kotlinx.coroutines.flow.Flow

class AnniversaryRepository(private val anniversaryDao: AnniversaryDao) {
    val allAnniversaries: Flow<List<Anniversary>> = anniversaryDao.getAllAnniversaries()

    suspend fun getAnniversaryById(id: Long): Anniversary? {
        return anniversaryDao.getAnniversaryById(id)
    }

    suspend fun getNextAnniversary(): Anniversary? {
        return anniversaryDao.getNextAnniversary()
    }

    suspend fun insertAnniversary(anniversary: Anniversary): Long {
        return anniversaryDao.insertAnniversary(anniversary)
    }

    suspend fun updateAnniversary(anniversary: Anniversary) {
        anniversaryDao.updateAnniversary(anniversary.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteAnniversary(anniversary: Anniversary) {
        anniversaryDao.deleteAnniversary(anniversary)
    }

    suspend fun deleteAnniversaryById(id: Long) {
        anniversaryDao.deleteAnniversaryById(id)
    }
}
