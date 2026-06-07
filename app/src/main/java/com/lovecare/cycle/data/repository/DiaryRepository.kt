package com.lovecare.cycle.data.repository

import com.lovecare.cycle.data.dao.DiaryDao
import com.lovecare.cycle.data.entity.DiaryEntry
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {
    val allDiaryEntries: Flow<List<DiaryEntry>> = diaryDao.getAllDiaryEntries()

    suspend fun getDiaryEntryById(id: Long): DiaryEntry? {
        return diaryDao.getDiaryEntryById(id)
    }

    suspend fun getLatestDiaryEntry(): DiaryEntry? {
        return diaryDao.getLatestDiaryEntry()
    }

    fun getDiaryEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<DiaryEntry>> {
        return diaryDao.getDiaryEntriesForDay(startOfDay, endOfDay)
    }

    suspend fun insertDiaryEntry(entry: DiaryEntry): Long {
        return diaryDao.insertDiaryEntry(entry)
    }

    suspend fun updateDiaryEntry(entry: DiaryEntry) {
        diaryDao.updateDiaryEntry(entry.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteDiaryEntry(entry: DiaryEntry) {
        diaryDao.deleteDiaryEntry(entry)
    }

    suspend fun deleteDiaryEntryById(id: Long) {
        diaryDao.deleteDiaryEntryById(id)
    }
}
