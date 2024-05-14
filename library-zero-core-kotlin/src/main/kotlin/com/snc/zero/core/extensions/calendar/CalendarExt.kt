package com.snc.zero.core.extensions.calendar

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.setYear(year: Int) {
    this.set(Calendar.YEAR, year)
}

fun Calendar.setMonth(month: Int) {
    this.set(Calendar.MONTH, month - 1)
}

fun Calendar.setDay(day: Int) {
    this.set(Calendar.DAY_OF_MONTH, day)
}

fun Calendar.setHour(hour: Int) {
    this.set(Calendar.HOUR_OF_DAY, hour)
}

fun Calendar.setMinute(minute: Int) {
    this.set(Calendar.MINUTE, minute)
}

fun Calendar.setSecond(second: Int) {
    this.set(Calendar.SECOND, second)
}

fun Calendar.setMillisecond(millisecond: Int) {
    this.set(Calendar.MILLISECOND, millisecond)
}

fun Calendar.startOfMonth() {
    this.setDay(1)
    this.startOfDay()
}

fun Calendar.startOfDay() {
    this.setHour(0)
    this.setMonth(0)
    this.setSecond(0)
    this.setMillisecond(0)
}

fun Calendar.endOfMonth() {
    val v = this.getLastDayOfMonth()
    this.setDay(v)
    endOfDay()
}

fun Calendar.endOfDay() {
    this.setHour(23)
    this.setMinute(59)
    this.setSecond(59)
    this.setMillisecond(999)
}

fun Calendar.addYear(amount: Int) {
    val v = this.getYear()
    this.setYear(v + amount)
}

fun Calendar.addMonth(amount: Int) {
    val v = this.getMonth()
    this.setMonth(v + amount)
}

fun Calendar.addDay(amount: Int) {
    val v = this.getDay()
    this.setDay(v + amount)
}

fun Calendar.addHour(amount: Int) {
    val v = this.getHour()
    this.setHour(v + amount)
}

fun Calendar.addMinute(amount: Int) {
    val v = this.getMinute()
    this.setMinute(v + amount)
}

fun Calendar.addSecond(amount: Int) {
    val v = this.getSecond()
    this.setSecond(v + amount)
}

fun Calendar.addMillisecond(amount: Int) {
    val v = this.getMillisecond()
    this.setMillisecond(v + amount)
}

fun Calendar.getYear(): Int {
    return this.get(Calendar.YEAR)
}

fun Calendar.getMonth(): Int {
    return this.get(Calendar.MONTH) + 1
}

fun Calendar.getDay(): Int {
    return this.get(Calendar.DAY_OF_MONTH)
}

fun Calendar.getHour(): Int {
    return this.get(Calendar.HOUR_OF_DAY)
}

fun Calendar.getMinute(): Int {
    return this.get(Calendar.MINUTE)
}

fun Calendar.getSecond(): Int {
    return this.get(Calendar.SECOND)
}

fun Calendar.getMillisecond(): Int {
    return this.get(Calendar.MILLISECOND)
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
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    val date: Date = format.parse(this) as Date
    return date.toCalendar()
}

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}