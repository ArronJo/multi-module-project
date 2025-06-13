package com.snc.zero.core.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 날짜 표현 방식
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class DateTimeFormat private constructor() {

    companion object {

        @Throws(ParseException::class)
        fun format(dateStr: String, inputFormat: String, outputFormat: String = "yyyyMMddHHmmss"): String {
            val input = SimpleDateFormat(inputFormat, Locale.getDefault())
            val date: Date = input.parse(dateStr) as Date
            val output = SimpleDateFormat(outputFormat, Locale.getDefault())
            return output.format(date)
        }

        @Throws(ParseException::class)
        fun format(calendar: Calendar, outputFormat: String = "yyyyMMddHHmmss"): String {
            val dateString = DateTimeParser.toString(calendar)
            return format(dateString, "yyyyMMddHHmmss", outputFormat)
        }

        fun format(date: Date, outputFormat: String = "yyyyMMddHHmmss"): String {
            val formatter = SimpleDateFormat(outputFormat, Locale.getDefault())
            return formatter.format(date)
        }
    }
}
