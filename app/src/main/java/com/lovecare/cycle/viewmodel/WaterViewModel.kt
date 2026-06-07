package com.lovecare.cycle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lovecare.cycle.LoveCycleApp
import com.lovecare.cycle.data.entity.WaterRecord
import com.lovecare.cycle.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WaterViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LoveCycleApp

    private val _todayTotal = MutableStateFlow(0)
    val todayTotal: StateFlow<Int> = _todayTotal.asStateFlow()

    private val _waterGoal = MutableStateFlow(2000)
    val waterGoal: StateFlow<Int> = _waterGoal.asStateFlow()

    private val _cupSize = MutableStateFlow(200)
    val cupSize: StateFlow<Int> = _cupSize.asStateFlow()

    val todayRecords: Flow<List<WaterRecord>> = flow {
        val startOfDay = DateUtils.getStartOfDay()
        val endOfDay = DateUtils.getEndOfDay()
        app.waterRepository.getWaterRecordsForDay(startOfDay, endOfDay).collect { records ->
            emit(records)
        }
    }

    init {
        viewModelScope.launch {
            app.settingsRepository.dailyWaterGoalMl.collect { goal ->
                _waterGoal.value = goal
            }
        }

        viewModelScope.launch {
            app.settingsRepository.cupSizeMl.collect { size ->
                _cupSize.value = size
            }
        }

        viewModelScope.launch {
            val startOfDay = DateUtils.getStartOfDay()
            val endOfDay = DateUtils.getEndOfDay()
            app.waterRepository.getTotalWaterForDay(startOfDay, endOfDay).collect { total ->
                _todayTotal.value = total ?: 0
            }
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            app.waterRepository.insertWaterRecord(WaterRecord(amountMl = amountMl))
        }
    }

    fun undoLastRecord() {
        viewModelScope.launch {
            val startOfDay = DateUtils.getStartOfDay()
            val endOfDay = DateUtils.getEndOfDay()
            val lastRecord = app.waterRepository.getLatestWaterRecordForDay(startOfDay, endOfDay)
            lastRecord?.let {
                app.waterRepository.deleteWaterRecord(it)
            }
        }
    }
}
