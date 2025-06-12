package com.snc.zero.core.date

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * DateTime 유틸리티
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class DateTimeParser private constructor() {

    companion object {

        @JvmStatic
        fun parseDate(dateString: String): Array<Int> {
            val year = dateString.substring(0, 4).toInt()
            val month = dateString.substring(4, 6).toInt()
            val day = dateString.substring(6, 8).toInt()
            return arrayOf(year, month, day)
        }

        @JvmStatic
        fun filter(dateStr: String): String {
            return dateStr.replace("(\\.|-|:|\\s)+".toRegex(), "")
        }

        @JvmStatic
        fun toString(calendar: Calendar): String {
            return String.format(
                Locale.getDefault(),
                "%04d%02d%02d%02d%02d%02d",
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.DATE],
                calendar[Calendar.HOUR_OF_DAY],
                calendar[Calendar.MINUTE],
                calendar[Calendar.SECOND]
            )
        }

        @JvmStatic
        fun today(): Calendar {
            return Calendar.getInstance()
        }

        @JvmStatic
        fun today(format: String): String {
            return DateTimeFormat.format(
                Calendar.getInstance(),
                outputFormat = format
            )
        }

        @JvmStatic
        fun today2(format: String): String {
            val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
            return simpleDateFormat.format(Calendar.getInstance().timeInMillis)
        }

        @JvmStatic
        fun tomorrow(): Calendar {
            val calendar = today()
            calendar.add(Calendar.DATE, 1)
            return calendar
        }

        @JvmStatic
        fun lastDayOfMonth(calendar: Calendar = Calendar.getInstance()): Int {
            // 대체 방법: 이번달 마지막 날짜, java.time.LocalDate.now().lengthOfMonth()
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
    }
}
