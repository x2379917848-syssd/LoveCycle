package com.lovecare.cycle.data.dao

import androidx.room.*
import com.lovecare.cycle.data.entity.DiaryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    fun getAllDiaryEntries(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getDiaryEntryById(id: Long): DiaryEntry?

    @Query("SELECT * FROM diary_entries ORDER BY date DESC LIMIT 1")
    suspend fun getLatestDiaryEntry(): DiaryEntry?

    @Query("SELECT * FROM diary_entries WHERE date >= :startOfDay AND date < :endOfDay ORDER BY date DESC")
    fun getDiaryEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<DiaryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(entry: DiaryEntry): Long

    @Update
    suspend fun updateDiaryEntry(entry: DiaryEntry)

    @Delete
    suspend fun deleteDiaryEntry(entry: DiaryEntry)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteDiaryEntryById(id: Long)
}
