package com.snc.zero.core.date

import com.snc.zero.core.calendar.CalendarConverter
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Date 비교
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class DateTimeCompare private constructor() {

    companion object {

        fun isBetweenDate(startDate: String, date: String, endDate: String): Int {
            if (compare(date, "<", startDate)) {
                return -1
            }
            if (compare(endDate, "<", date)) {
                return 1
            }
            return 0
        }

        fun compare(data1: String, sign: String, data2: String): Boolean {
            return when (sign) {
                ">=" -> {
                    data1 >= data2
                }
                "<=" -> {
                    data1 <= data2
                }
                "==", "=" -> {
                    data1.compareTo(data2) == 0
                }
                "!=" -> {
                    data1.compareTo(data2) != 0
                }
                ">" -> {
                    data1 > data2
                }
                "<" -> {
                    data1 < data2
                }
                else -> false
            }
        }

        fun betweenDays(startDate: Calendar, endDate: Calendar): Int {
            val start = startDate.timeInMillis
            val end = endDate.timeInMillis

            var presumedDays = TimeUnit.MILLISECONDS.toDays(end - start).toInt()
            startDate.add(Calendar.DAY_OF_MONTH, presumedDays)

            if (startDate.before(endDate)) {
                startDate.add(Calendar.DAY_OF_MONTH, 1)
                ++presumedDays
            }
            if (startDate.after(endDate)) {
                --presumedDays
            }
            return presumedDays
        }

        fun betweenDays(srcDate: String, dstDate: String): Int {
            val srcCalendar = CalendarConverter.toCalendar(srcDate)
            val dstCalendar = CalendarConverter.toCalendar(dstDate)
            return betweenDays(srcCalendar, dstCalendar)
        }
    }
}
