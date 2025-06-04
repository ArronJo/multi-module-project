package com.snc.zero.extension.calendar

import com.snc.zero.logger.jvm.TLogging
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

private val logger = TLogging.logger { }

fun Calendar.setYear(year: Int): Calendar {
    this[Calendar.YEAR] = year
    return this
}

fun Calendar.setMonth(month: Int): Calendar {
    this[Calendar.MONTH] = month - 1
    return this
}

fun Calendar.setDay(day: Int): Calendar {
    this[Calendar.DAY_OF_MONTH] = day
    return this
}

fun Calendar.setHour(hour: Int): Calendar {
    this[Calendar.HOUR_OF_DAY] = hour
    return this
}

fun Calendar.setMinute(minute: Int): Calendar {
    this[Calendar.MINUTE] = minute
    return this
}

fun Calendar.setSecond(second: Int): Calendar {
    this[Calendar.SECOND] = second
    return this
}

fun Calendar.setMillisecond(millisecond: Int): Calendar {
    this[Calendar.MILLISECOND] = millisecond
    return this
}

fun Calendar.startOfMonth(): Calendar {
    this.setDay(1)
    this.startOfDay()
    return this
}

fun Calendar.startOfDay(): Calendar {
    this.setHour(0)
    this.setMonth(0)
    this.setSecond(0)
    this.setMillisecond(0)
    return this
}

fun Calendar.endOfMonth(): Calendar {
    val v = this.getLastDayOfMonth()
    this.setDay(v)
    endOfDay()
    return this
}

fun Calendar.endOfDay(): Calendar {
    this.setHour(23)
    this.setMinute(59)
    this.setSecond(59)
    this.setMillisecond(999)
    return this
}

fun Calendar.addYear(amount: Int): Calendar {
    val v = this.getYear()
    this.setYear(v + amount)
    return this
}

fun Calendar.addMonth(amount: Int): Calendar {
    val v = this.getMonth()
    this.setMonth(v + amount)
    return this
}

fun Calendar.addDay(amount: Int): Calendar {
    val v = this.getDay()
    this.setDay(v + amount)
    return this
}

fun Calendar.addHour(amount: Int): Calendar {
    val v = this.getHour()
    this.setHour(v + amount)
    return this
}

fun Calendar.addMinute(amount: Int): Calendar {
    val v = this.getMinute()
    this.setMinute(v + amount)
    return this
}

fun Calendar.addSecond(amount: Int): Calendar {
    val v = this.getSecond()
    this.setSecond(v + amount)
    return this
}

fun Calendar.addMillisecond(amount: Int): Calendar {
    val v = this.getMillisecond()
    this.setMillisecond(v + amount)
    return this
}

fun Calendar.getYear(): Int {
    return this[Calendar.YEAR]
}

fun Calendar.getMonth(): Int {
    return this[Calendar.MONTH] + 1
}

fun Calendar.getDay(): Int {
    return this[Calendar.DAY_OF_MONTH]
}

fun Calendar.getHour(): Int {
    return this[Calendar.HOUR_OF_DAY]
}

fun Calendar.getMinute(): Int {
    return this[Calendar.MINUTE]
}

fun Calendar.getSecond(): Int {
    return this[Calendar.SECOND]
}

fun Calendar.getMillisecond(): Int {
    return this[Calendar.MILLISECOND]
}

fun Calendar.getLastDayOfMonth(): Int {
    return this.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun Calendar.diff(endDateTime: Calendar): Long {
    val differenceInMilliSeconds = endDateTime.getTimeInMillis() - this.getTimeInMillis()
    var differenceInDays = (differenceInMilliSeconds.toFloat() / (24 * 60 * 60 * 1000)).toLong()
    if (differenceInDays < 0) {
        differenceInDays += 365
    }
    return differenceInDays
}

fun String.toCalendar(pattern: String = "yyyyMMddHHmmss"): Calendar {
    val sdf = SimpleDateFormat(pattern)
    val calendar = Calendar.getInstance()
    try {
        val date = sdf.parse(this.substring(0, min(pattern.length, this.length)))
        calendar.time = date
    } catch (e: ParseException) {
        logger.error { e }
    }
    return calendar
}

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}
