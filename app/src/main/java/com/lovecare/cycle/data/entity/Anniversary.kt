package com.lovecare.cycle.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anniversaries")
data class Anniversary(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
