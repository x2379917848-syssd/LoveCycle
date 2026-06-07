package com.lovecare.cycle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lovecare.cycle.LoveCycleApp
import com.lovecare.cycle.data.entity.PeriodRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PeriodViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LoveCycleApp

    val allPeriods: Flow<List<PeriodRecord>> = app.periodRepository.allPeriods

    private val _editingPeriod = MutableStateFlow<PeriodRecord?>(null)
    val editingPeriod: Flow<PeriodRecord?> = _editingPeriod.asStateFlow()

    fun loadPeriod(id: Long) {
        viewModelScope.launch {
            _editingPeriod.value = app.periodRepository.getPeriodById(id)
        }
    }

    fun clearEditing() {
        _editingPeriod.value = null
    }

    fun savePeriod(
        startDate: Long,
        endDate: Long?,
        flowLevel: String,
        painLevel: Int,
        mood: String,
        symptoms: String,
        note: String,
        existingId: Long? = null
    ) {
        viewModelScope.launch {
            if (existingId != null) {
                val period = PeriodRecord(
                    id = existingId,
                    startDate = startDate,
                    endDate = endDate,
                    flowLevel = flowLevel,
                    painLevel = painLevel,
                    mood = mood,
                    symptoms = symptoms,
                    note = note,
                    updatedAt = System.currentTimeMillis()
                )
                app.periodRepository.updatePeriod(period)
            } else {
                val period = PeriodRecord(
                    startDate = startDate,
                    endDate = endDate,
                    flowLevel = flowLevel,
                    painLevel = painLevel,
                    mood = mood,
                    symptoms = symptoms,
                    note = note
                )
                app.periodRepository.insertPeriod(period)
            }
            _editingPeriod.value = null
        }
    }

    fun deletePeriod(id: Long) {
        viewModelScope.launch {
            app.periodRepository.deletePeriodById(id)
        }
    }
}
