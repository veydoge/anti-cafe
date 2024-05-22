package com.example.anti_cafe.utils

import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.yearMonth
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

fun getWeekPageTitle(week: Week): String {
    val firstDate = week.days.first().date
    val lastDate = week.days.last().date
    return when {
        firstDate.yearMonth == lastDate.yearMonth -> {
            firstDate.yearMonth.displayText(short = false)
        }
        firstDate.year == lastDate.year -> {
            "${firstDate.month.displayText(short = false)} - ${lastDate.yearMonth.displayText()}"
        }
        else -> {
            "${firstDate.yearMonth.displayText()} - ${lastDate.yearMonth.displayText()}"
        }
    }
}

fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL_STANDALONE
    return getDisplayName(style, Locale("ru")).capitalize()
}
