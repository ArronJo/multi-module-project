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

        fun toCalendar(dateString: String): Calendar {
            // 입력 문자열에서 숫자만 추출
            val numbersOnly = dateString.replace(Regex("[^0-9]"), "")

            val calendar = Calendar.getInstance()

            when (numbersOnly.length) {
                6 -> {
                    // yyMMdd 형식
                    val fullYear = 1900 + numbersOnly.substring(0, 2).toInt()
                    calendar[Calendar.YEAR] = fullYear
                    calendar[Calendar.MONTH] = numbersOnly.substring(2, 4).toInt() - 1
                    calendar[Calendar.DAY_OF_MONTH] = numbersOnly.substring(4, 6).toInt()
                    calendar[Calendar.HOUR_OF_DAY] = 0
                    calendar[Calendar.MINUTE] = 0
                    calendar[Calendar.SECOND] = 0
                }
                8 -> {
                    // yyyyMMdd 형식
                    calendar[Calendar.YEAR] = numbersOnly.substring(0, 4).toInt()
                    calendar[Calendar.MONTH] = numbersOnly.substring(4, 6).toInt() - 1
                    calendar[Calendar.DAY_OF_MONTH] = numbersOnly.substring(6, 8).toInt()
                    calendar[Calendar.HOUR_OF_DAY] = 0
                    calendar[Calendar.MINUTE] = 0
                    calendar[Calendar.SECOND] = 0
                }
                10 -> {
                    // yyMMddHHmm 형식
                    calendar[Calendar.YEAR] = 1900 + numbersOnly.substring(0, 2).toInt()
                    calendar[Calendar.MONTH] = numbersOnly.substring(2, 4).toInt() - 1
                    calendar[Calendar.DAY_OF_MONTH] = numbersOnly.substring(4, 6).toInt()
                    calendar[Calendar.HOUR_OF_DAY] = numbersOnly.substring(6, 8).toInt()
                    calendar[Calendar.MINUTE] = numbersOnly.substring(8, 10).toInt()
                }
                12 -> {
                    // yyMMddHHmmss 형식
                    val fullYear = 1900 + numbersOnly.substring(0, 2).toInt()
                    calendar[Calendar.YEAR] = fullYear
                    calendar[Calendar.MONTH] = numbersOnly.substring(2, 4).toInt() - 1
                    calendar[Calendar.DAY_OF_MONTH] = numbersOnly.substring(4, 6).toInt()
                    calendar[Calendar.HOUR_OF_DAY] = numbersOnly.substring(6, 8).toInt()
                    calendar[Calendar.MINUTE] = numbersOnly.substring(8, 10).toInt()
                    calendar[Calendar.SECOND] = numbersOnly.substring(10, 12).toInt()
                }
                14 -> {
                    // yyyyMMddHHmmss 형식
                    calendar[Calendar.YEAR] = numbersOnly.substring(0, 4).toInt()
                    calendar[Calendar.MONTH] = numbersOnly.substring(4, 6).toInt() - 1
                    calendar[Calendar.DAY_OF_MONTH] = numbersOnly.substring(6, 8).toInt()
                    calendar[Calendar.HOUR_OF_DAY] = numbersOnly.substring(8, 10).toInt()
                    calendar[Calendar.MINUTE] = numbersOnly.substring(10, 12).toInt()
                    calendar[Calendar.SECOND] = numbersOnly.substring(12, 14).toInt()
                }
                else -> {
                    throw IllegalArgumentException("지원하지 않는 날짜 형식입니다. 6, 8, 10, 12, 14자리만 지원합니다: $dateString (추출된 숫자: $numbersOnly)")
                }
            }

            // 밀리초는 항상 0으로 설정
            calendar[Calendar.MILLISECOND] = 0

            return calendar
        }

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

        fun toCalendar(year: String, month: String, day: String): Calendar? {
            return toCalendar(year.toInt(), month.toInt(), day.toInt())
        }

        fun toDateString(calendar: Calendar, format: String): String {
            val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
            return simpleDateFormat.format(calendar.timeInMillis)
        }
    }
}
