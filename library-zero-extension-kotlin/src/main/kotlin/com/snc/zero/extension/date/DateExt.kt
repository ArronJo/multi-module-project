package com.snc.zero.extension.date

import java.text.SimpleDateFormat
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
