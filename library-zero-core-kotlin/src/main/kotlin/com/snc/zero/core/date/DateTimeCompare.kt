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
            val startCopy = startDate.clone() as Calendar
            val endCopy = endDate.clone() as Calendar

            // 날짜 부분만 비교 (시간 무시)
            startCopy[Calendar.HOUR_OF_DAY] = 0
            startCopy[Calendar.MINUTE] = 0
            startCopy[Calendar.SECOND] = 0
            startCopy[Calendar.MILLISECOND] = 0

            endCopy[Calendar.HOUR_OF_DAY] = 0
            endCopy[Calendar.MINUTE] = 0
            endCopy[Calendar.SECOND] = 0
            endCopy[Calendar.MILLISECOND] = 0

            val diffMillis = endCopy.timeInMillis - startCopy.timeInMillis
            return TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
        }

        fun betweenDays(srcDate: String, dstDate: String): Int {
            val srcCalendar = CalendarConverter.toCalendar(srcDate)
            val dstCalendar = CalendarConverter.toCalendar(dstDate)
            return betweenDays(srcCalendar, dstCalendar)
        }
    }
}
