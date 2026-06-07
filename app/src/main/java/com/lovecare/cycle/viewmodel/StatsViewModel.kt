package com.lovecare.cycle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lovecare.cycle.LoveCycleApp
import com.lovecare.cycle.data.entity.PeriodRecord
import com.lovecare.cycle.data.entity.WaterRecord
import com.lovecare.cycle.data.entity.DiaryEntry
import com.lovecare.cycle.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class StatsUiState(
    val avgCycleLength: Int = 28,
    val avgPeriodLength: Int = 5,
    val commonSymptoms: List<String> = emptyList(),
    val avgPainLevel: Float = 0f,
    val waterCompletionRate: Float = 0f,
    val waterTrend: List<Int> = emptyList(),
    val moodDistribution: Map<String, Int> = emptyMap()
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LoveCycleApp

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val periods = app.periodRepository.getRecentPeriods(10)
            if (periods.isNotEmpty()) {
                val cycleLengths = mutableListOf<Int>()
                val periodLengths = mutableListOf<Int>()
                val painLevels = mutableListOf<Int>()
                val symptoms = mutableListOf<String>()

                for (i in 0 until minOf(periods.size - 1, 6)) {
                    val cycleLength = ((periods[i].startDate - periods[i + 1].startDate) / (24 * 60 * 60 * 1000L)).toInt()
                    if (cycleLength in 15..60) {
                        cycleLengths.add(cycleLength)
                    }
                }

                for (period in periods) {
                    if (period.endDate != null) {
                        val periodLength = ((period.endDate - period.startDate) / (24 * 60 * 60 * 1000L)).toInt() + 1
                        periodLengths.add(periodLength)
                    }
                    painLevels.add(period.painLevel)
                    if (period.symptoms.isNotBlank()) {
                        symptoms.addAll(period.symptoms.split(",").map { it.trim() })
                    }
                }

                val avgCycle = if (cycleLengths.isNotEmpty()) cycleLengths.average().toInt() else 28
                val avgPeriod = if (periodLengths.isNotEmpty()) periodLengths.average().toInt() else 5
                val avgPain = if (painLevels.isNotEmpty()) painLevels.average().toFloat() else 0f
                val symptomCounts = symptoms.groupingBy { it }.eachCount()
                val commonSymptoms = symptomCounts.entries.sortedByDescending { it.value }.take(5).map { it.key }

                _uiState.value = _uiState.value.copy(
                    avgCycleLength = avgCycle,
                    avgPeriodLength = avgPeriod,
                    avgPainLevel = avgPain,
                    commonSymptoms = commonSymptoms
                )
            }
        }

        viewModelScope.launch {
            val startOfDay = DateUtils.getStartOfDay()
            val endOfDay = DateUtils.getEndOfDay()
            val goal = app.settingsRepository.dailyWaterGoalMl.first()
            app.waterRepository.getTotalWaterForDay(startOfDay, endOfDay).collect { total ->
                val rate = if (goal > 0) (total ?: 0).toFloat() / goal else 0f
                _uiState.value = _uiState.value.copy(waterCompletionRate = rate.coerceIn(0f, 1f))
            }
        }

        viewModelScope.launch {
            val waterTrend = mutableListOf<Int>()
            for (i in 6 downTo 0) {
                val dayStart = DateUtils.getStartOfDaysAgo(i)
                val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1
                val records = app.waterRepository.getRecentWaterRecordsForDay(dayStart, dayEnd, 100)
                waterTrend.add(records.sumOf { it.amountMl })
            }
            _uiState.value = _uiState.value.copy(waterTrend = waterTrend)
        }

        viewModelScope.launch {
            app.diaryRepository.allDiaryEntries.collect { entries ->
                val moodCounts = entries.groupingBy { it.mood }.eachCount()
                _uiState.value = _uiState.value.copy(moodDistribution = moodCounts)
            }
        }
    }
}
