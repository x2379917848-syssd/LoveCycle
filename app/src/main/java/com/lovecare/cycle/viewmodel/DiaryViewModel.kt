package com.lovecare.cycle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lovecare.cycle.LoveCycleApp
import com.lovecare.cycle.data.entity.DiaryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LoveCycleApp

    val allDiaryEntries: Flow<List<DiaryEntry>> = app.diaryRepository.allDiaryEntries

    private val _editingEntry = MutableStateFlow<DiaryEntry?>(null)
    val editingEntry: Flow<DiaryEntry?> = _editingEntry.asStateFlow()

    fun loadEntry(id: Long) {
        viewModelScope.launch {
            _editingEntry.value = app.diaryRepository.getDiaryEntryById(id)
        }
    }

    fun clearEditing() {
        _editingEntry.value = null
    }

    fun saveEntry(
        date: Long,
        mood: String,
        content: String,
        tags: String,
        existingId: Long? = null
    ) {
        viewModelScope.launch {
            if (existingId != null) {
                val entry = DiaryEntry(
                    id = existingId,
                    date = date,
                    mood = mood,
                    content = content,
                    tags = tags,
                    updatedAt = System.currentTimeMillis()
                )
                app.diaryRepository.updateDiaryEntry(entry)
            } else {
                val entry = DiaryEntry(
                    date = date,
                    mood = mood,
                    content = content,
                    tags = tags
                )
                app.diaryRepository.insertDiaryEntry(entry)
            }
            _editingEntry.value = null
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            app.diaryRepository.deleteDiaryEntryById(id)
        }
    }
}
