package com.lovecare.cycle.util

import com.lovecare.cycle.data.entity.PeriodRecord
import java.util.*

data class PeriodPredictionResult(
    val averageCycleLength: Int = 28,
    val averagePeriodLength: Int = 5,
    val currentCycleDay: Int? = null,
    val lastPeriodStartDate: Long? = null,
    val nextPeriodStartDate: Long? = null,
    val nextPeriodEndDate: Long? = null,
    val daysUntilNextPeriod: Int? = null,
    val fertileWindowStart: Long? = null,
    val fertileWindowEnd: Long? = null,
    val ovulationDate: Long? = null,
    val confidenceText: String = "数据不足",
    val medicalDisclaimer: String = "经期和排卵期预测仅供参考，不作为医疗建议。如果身体明显不适，请及时咨询医生。"
)

object PeriodPredictionUtil {
    private const val MIN_CYCLE_LENGTH = 15
    private const val MAX_CYCLE_LENGTH = 60
    private const val DEFAULT_CYCLE_LENGTH = 28
    private const val DEFAULT_PERIOD_DURATION = 5
    private const val FERTILE_WINDOW_BEFORE_OVULATION = 5
    private const val FERTILE_WINDOW_AFTER_OVULATION = 3

    fun calculatePrediction(
        periodRecords: List<PeriodRecord>,
        defaultCycleLength: Int = DEFAULT_CYCLE_LENGTH,
        defaultPeriodDuration: Int = DEFAULT_PERIOD_DURATION
    ): PeriodPredictionResult {
        if (periodRecords.isEmpty()) {
            return PeriodPredictionResult(
                averageCycleLength = defaultCycleLength,
                averagePeriodLength = defaultPeriodDuration,
                confidenceText = "还没有经期记录"
            )
        }

        val sortedRecords = periodRecords.sortedByDescending { it.startDate }
        val lastPeriod = sortedRecords.firstOrNull() ?: return PeriodPredictionResult(
            averageCycleLength = defaultCycleLength,
            averagePeriodLength = defaultPeriodDuration,
            confidenceText = "还没有经期记录"
        )

        val lastStartDate = lastPeriod.startDate
        val now = System.currentTimeMillis()

        if (sortedRecords.size < 2) {
            val nextStart = lastStartDate + (defaultCycleLength * 24 * 60 * 60 * 1000L)
            val nextEnd = nextStart + (defaultPeriodDuration * 24 * 60 * 60 * 1000L)
            val daysUntil = ((nextStart - now) / (24 * 60 * 60 * 1000L)).toInt()
            val cycleDay = ((now - lastStartDate) / (24 * 60 * 60 * 1000L)).toInt() + 1

            return PeriodPredictionResult(
                averageCycleLength = defaultCycleLength,
                averagePeriodLength = defaultPeriodDuration,
                currentCycleDay = cycleDay,
                lastPeriodStartDate = lastStartDate,
                nextPeriodStartDate = nextStart,
                nextPeriodEndDate = nextEnd,
                daysUntilNextPeriod = daysUntil,
                fertileWindowStart = nextStart - (14 * 24 * 60 * 60 * 1000L),
                fertileWindowEnd = nextStart - (10 * 24 * 60 * 60 * 1000L),
                ovulationDate = nextStart - (14 * 24 * 60 * 60 * 1000L),
                confidenceText = "基于1次记录估算，可能有偏差"
            )
        }

        val cycleLengths = mutableListOf<Int>()
        val periodLengths = mutableListOf<Int>()

        for (i in 0 until minOf(sortedRecords.size - 1, 6)) {
            val currentStart = sortedRecords[i].startDate
            val nextStart = sortedRecords[i + 1].startDate
            val cycleLength = ((currentStart - nextStart) / (24 * 60 * 60 * 1000L)).toInt()

            if (cycleLength in MIN_CYCLE_LENGTH..MAX_CYCLE_LENGTH) {
                cycleLengths.add(cycleLength)
            }
        }

        for (record in sortedRecords) {
            if (record.endDate != null) {
                val periodLength = ((record.endDate - record.startDate) / (24 * 60 * 60 * 1000L)).toInt() + 1
                periodLengths.add(periodLength)
            }
        }

        val avgCycleLength = if (cycleLengths.isNotEmpty()) {
            cycleLengths.average().toInt()
        } else {
            defaultCycleLength
        }

        val avgPeriodLength = if (periodLengths.isNotEmpty()) {
            periodLengths.average().toInt()
        } else {
            defaultPeriodDuration
        }

        val nextStartDate = lastStartDate + (avgCycleLength * 24 * 60 * 60 * 1000L)
        val nextEndDate = nextStartDate + (avgPeriodLength * 24 * 60 * 60 * 1000L)
        val daysUntil = ((nextStartDate - now) / (24 * 60 * 60 * 1000L)).toInt()
        val currentCycleDay = ((now - lastStartDate) / (24 * 60 * 60 * 1000L)).toInt() + 1

        val ovulationDate = nextStartDate - (14 * 24 * 60 * 60 * 1000L)
        val fertileStart = ovulationDate - (FERTILE_WINDOW_BEFORE_OVULATION * 24 * 60 * 60 * 1000L)
        val fertileEnd = ovulationDate + (FERTILE_WINDOW_AFTER_OVULATION * 24 * 60 * 60 * 1000L)

        val confidenceText = when {
            cycleLengths.size >= 3 -> "基于${cycleLengths.size}次周期计算，较为可靠"
            cycleLengths.size >= 1 -> "基于${cycleLengths.size}次周期估算"
            else -> "数据不足，仅供参考"
        }

        return PeriodPredictionResult(
            averageCycleLength = avgCycleLength,
            averagePeriodLength = avgPeriodLength,
            currentCycleDay = if (currentCycleDay > 0) currentCycleDay else null,
            lastPeriodStartDate = lastStartDate,
            nextPeriodStartDate = nextStartDate,
            nextPeriodEndDate = nextEndDate,
            daysUntilNextPeriod = daysUntil,
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileEnd,
            ovulationDate = ovulationDate,
            confidenceText = confidenceText
        )
    }
}
