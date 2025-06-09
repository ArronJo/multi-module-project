package com.snc.zero.extension.date

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun String.parseDate(pattern: String = "yyyyMMdd"): Date? {
    try {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.parse(this)
    } catch (e: Exception) {
        println("Invalid date format: $this, $e")
        return null
    }
}

fun String.parseDateTime(pattern: String = "yyyyMMdd"): Date? {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val localDate = LocalDate.parse(this, formatter)
    val zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault())
    return Date.from(zonedDateTime.toInstant())
}
