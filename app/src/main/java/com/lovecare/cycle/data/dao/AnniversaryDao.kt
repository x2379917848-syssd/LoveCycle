package com.lovecare.cycle.data.dao

import androidx.room.*
import com.lovecare.cycle.data.entity.Anniversary
import kotlinx.coroutines.flow.Flow

@Dao
interface AnniversaryDao {
    @Query("SELECT * FROM anniversaries ORDER BY date ASC")
    fun getAllAnniversaries(): Flow<List<Anniversary>>

    @Query("SELECT * FROM anniversaries WHERE id = :id")
    suspend fun getAnniversaryById(id: Long): Anniversary?

    @Query("SELECT * FROM anniversaries ORDER BY date ASC LIMIT 1")
    suspend fun getNextAnniversary(): Anniversary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnniversary(anniversary: Anniversary): Long

    @Update
    suspend fun updateAnniversary(anniversary: Anniversary)

    @Delete
    suspend fun deleteAnniversary(anniversary: Anniversary)

    @Query("DELETE FROM anniversaries WHERE id = :id")
    suspend fun deleteAnniversaryById(id: Long)
}
