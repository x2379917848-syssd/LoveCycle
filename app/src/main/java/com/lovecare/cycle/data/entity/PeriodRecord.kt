package com.lovecare.cycle.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period_records")
data class PeriodRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: Long,
    val endDate: Long? = null,
    val flowLevel: String = "medium",
    val painLevel: Int = 0,
    val mood: String = "",
    val symptoms: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
