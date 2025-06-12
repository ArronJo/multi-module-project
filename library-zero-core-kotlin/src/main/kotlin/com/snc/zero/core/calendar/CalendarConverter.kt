package com.snc.zero.core.calendar

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Calendar 변형
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class CalendarConverter private constructor() {

    companion object {

        @JvmStatic
        fun toCalendar(dateString: String): Calendar {
            val calendar = Calendar.getInstance()
            calendar[Calendar.SECOND] =
                if (dateString.length >= 14) dateString.substring(12, 14).toInt() else 0
            calendar[Calendar.MINUTE] =
                if (dateString.length >= 12) dateString.substring(10, 12).toInt() else 0
            calendar[Calendar.HOUR_OF_DAY] =
                if (dateString.length >= 10) dateString.substring(8, 10).toInt() else 0
            calendar[Calendar.DAY_OF_MONTH] =
                if (dateString.length >= 8) dateString.substring(6, 8).toInt() else 1
            calendar[Calendar.MONTH] =
                if (dateString.length >= 6) dateString.substring(4, 6).toInt() - 1 else 0
            calendar[Calendar.YEAR] =
                if (dateString.length >= 4) dateString.substring(0, 4).toInt() else 1900
            return calendar
        }

        @JvmStatic
        fun toCalendar(year: Int, month: Int, day: Int): Calendar? {
            val calendar = Calendar.getInstance()
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.HOUR_OF_DAY] = 1
            calendar[Calendar.DAY_OF_MONTH] = day
            calendar[Calendar.MONTH] = month - 1
            calendar[Calendar.YEAR] = year
            return calendar
        }

        @JvmStatic
        fun toCalendar(year: String, month: String, day: String): Calendar? {
            return toCalendar(year.toInt(), month.toInt(), day.toInt())
        }

        @JvmStatic
        fun toDateString(calendar: Calendar, format: String): String {
            val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
            return simpleDateFormat.format(calendar.timeInMillis)
        }
    }
}
