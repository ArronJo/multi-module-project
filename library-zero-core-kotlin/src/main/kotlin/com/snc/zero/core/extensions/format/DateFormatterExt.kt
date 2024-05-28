package com.snc.zero.core.extensions.format

import java.text.SimpleDateFormat
import java.util.*

fun String.formatDateTime(inputFormat: String, outputFormat: String = "yyyyMMddHHmmss"): String {
    val input = SimpleDateFormat(inputFormat, Locale.getDefault())
    val date: Date = input.parse(this) as Date
    val output = SimpleDateFormat(outputFormat, Locale.getDefault())
    return output.format(date)
}

fun Calendar.formatDateTime(outputFormat: String = "yyyyMMddHHmmss"): String {
    var v = outputFormat
    v = v.replace("yyyy".toRegex(), this[Calendar.YEAR].toString())
    v = v.replace("yy".toRegex(), this[Calendar.YEAR].toString().substring(2, 4))
    v = v.replace("MM".toRegex(), (this[Calendar.MONTH] + 1).toString().padStart(2, '0'))
    v = v.replace("dd".toRegex(), this[Calendar.DAY_OF_MONTH].toString().padStart(2, '0'))
    v = v.replace("HH".toRegex(), this[Calendar.HOUR_OF_DAY].toString().padStart(2, '0'))
    v = v.replace("mm".toRegex(), this[Calendar.MINUTE].toString().padStart(2, '0'))
    v = v.replace("ss".toRegex(), this[Calendar.SECOND].toString().padStart(2, '0'))
    return v
}

fun Date.formatDateTime(outputFormat: String = "yyyyMMddHHmmss"): String {
    val formatter = SimpleDateFormat(outputFormat, Locale.getDefault())
    return formatter.format(this)
}
