package com.lovecare.cycle.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val monthDayFormat = SimpleDateFormat("MM月dd日", Locale.CHINESE)
    private val dayFormat = SimpleDateFormat("dd日", Locale.CHINESE)
    private val weekdayFormat = SimpleDateFormat("EEEE", Locale.CHINESE)

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatMonthDay(timestamp: Long): String {
        return monthDayFormat.format(Date(timestamp))
    }

    fun formatDay(timestamp: Long): String {
        return dayFormat.format(Date(timestamp))
    }

    fun formatWeekday(timestamp: Long): String {
        return weekdayFormat.format(Date(timestamp))
    }

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    fun getStartOfDaysAgo(days: Int): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getDaysUntil(targetTimestamp: Long, fromTimestamp: Long = System.currentTimeMillis()): Int {
        val diff = targetTimestamp - fromTimestamp
        return (diff / (24 * 60 * 60 * 1000L)).toInt()
    }

    fun getDaysSince(timestamp: Long, fromTimestamp: Long = System.currentTimeMillis()): Int {
        val diff = fromTimestamp - timestamp
        return (diff / (24 * 60 * 60 * 1000L)).toInt()
    }

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isToday(timestamp: Long): Boolean {
        return isSameDay(timestamp, System.currentTimeMillis())
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getNextYearDate(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }

    fun getDaysUntilNextOccurrence(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        var target = timestamp

        while (target <= now) {
            target = getNextYearDate(target)
        }

        return getDaysUntil(target, now)
    }
}
