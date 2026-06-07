package com.lovecare.cycle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lovecare.cycle.LoveCycleApp
import com.lovecare.cycle.data.entity.PeriodRecord
import com.lovecare.cycle.data.entity.WaterRecord
import com.lovecare.cycle.data.entity.DiaryEntry
import com.lovecare.cycle.data.entity.Anniversary
import com.lovecare.cycle.util.DateUtils
import com.lovecare.cycle.util.PeriodPredictionResult
import com.lovecare.cycle.util.PeriodPredictionUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as LoveCycleApp

    private val _prediction = MutableStateFlow(PeriodPredictionResult())
    val prediction: StateFlow<PeriodPredictionResult> = _prediction.asStateFlow()

    private val _todayWater = MutableStateFlow(0)
    val todayWater: StateFlow<Int> = _todayWater.asStateFlow()

    private val _waterGoal = MutableStateFlow(2000)
    val waterGoal: StateFlow<Int> = _waterGoal.asStateFlow()

    val latestDiary: Flow<DiaryEntry?> = flow {
        emit(app.diaryRepository.getLatestDiaryEntry())
    }

    val nextAnniversary: Flow<Anniversary?> = flow {
        emit(app.anniversaryRepository.getNextAnniversary())
    }

    val careMessages = listOf(
        "今天也要好好照顾自己",
        "记得喝水哦",
        "不舒服的话就慢慢来",
        "你已经做得很好啦",
        "今天也辛苦啦",
        "记得好好吃饭",
        "如果身体不舒服，请及时休息"
    )

    private val _todayCareMessage = MutableStateFlow(careMessages[0])
    val todayCareMessage: StateFlow<String> = _todayCareMessage.asStateFlow()

    init {
        viewModelScope.launch {
            app.settingsRepository.dailyWaterGoalMl.collect { goal ->
                _waterGoal.value = goal
            }
        }

        viewModelScope.launch {
            val startOfDay = DateUtils.getStartOfDay()
            val endOfDay = DateUtils.getEndOfDay()
            app.waterRepository.getTotalWaterForDay(startOfDay, endOfDay).collect { total ->
                _todayWater.value = total ?: 0
            }
        }

        viewModelScope.launch {
            app.periodRepository.allPeriods.collect { periods ->
                val defaultCycle = _waterGoal.value
                val defaultDuration = 5
                _prediction.value = PeriodPredictionUtil.calculatePrediction(periods, defaultCycle, defaultDuration)
            }
        }

        viewModelScope.launch {
            val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
            val index = dayOfYear % careMessages.size
            _todayCareMessage.value = careMessages[index]
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            app.waterRepository.insertWaterRecord(WaterRecord(amountMl = amountMl))
        }
    }
}
