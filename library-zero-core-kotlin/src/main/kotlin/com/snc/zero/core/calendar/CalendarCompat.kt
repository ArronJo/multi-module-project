package com.snc.zero.core.calendar

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Calendar Interface
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class CalendarCompat private constructor(var cal: Calendar) {

    companion object {

        fun with(cal: Calendar = Calendar.getInstance()): CalendarCompat {
            return CalendarCompat(cal)
        }
    }

    init {
        setYear(1900)
        setMonth(1)
        setDay(1)
        setHour(0)
        setMinute(0)
        setSecond(0)
    }

    fun setCalendar(dateString: String): CalendarCompat {
        cal = CalendarConverter.toCalendar(dateString)
        return this
    }

    fun setYear(year: Int): CalendarCompat {
        cal[Calendar.YEAR] = year
        return this
    }

    fun setMonth(month: Int): CalendarCompat {
        cal[Calendar.MONTH] = month - 1
        return this
    }

    fun setDay(day: Int): CalendarCompat {
        cal[Calendar.DAY_OF_MONTH] = day
        return this
    }

    fun setHour(hour: Int): CalendarCompat {
        cal[Calendar.HOUR_OF_DAY] = hour
        return this
    }

    fun setMinute(minute: Int): CalendarCompat {
        cal[Calendar.MINUTE] = minute
        return this
    }

    fun setSecond(second: Int): CalendarCompat {
        cal[Calendar.SECOND] = second
        return this
    }

    fun setMillisecond(millisecond: Int): CalendarCompat {
        cal[Calendar.MILLISECOND] = millisecond
        return this
    }

    fun setTimeInMillis(millis: Int): CalendarCompat {
        cal.timeInMillis = millis.toLong()
        return this
    }

    fun startOfMonth(): CalendarCompat {
        this.setDay(1)
        this.startOfDay()
        return this
    }

    fun startOfDay(): CalendarCompat {
        this.setHour(0)
        this.setMinute(0)
        this.setSecond(0)
        this.setMillisecond(0)
        return this
    }

    fun endOfMonth(): CalendarCompat {
        val v = this.getLastDayOfMonth()
        this.setDay(v)
        this.endOfDay()
        return this
    }

    fun endOfDay(): CalendarCompat {
        this.setHour(23)
        this.setMinute(59)
        this.setSecond(59)
        this.setMillisecond(999)
        return this
    }

    fun addYear(amount: Int): CalendarCompat {
        cal[Calendar.YEAR] = cal[Calendar.YEAR] + amount
        return this
    }

    fun addMonth(amount: Int): CalendarCompat {
        cal[Calendar.MONTH] = cal[Calendar.MONTH] + amount
        return this
    }

    fun addDay(amount: Int): CalendarCompat {
        cal[Calendar.DAY_OF_MONTH] = cal[Calendar.DAY_OF_MONTH] + amount
        return this
    }

    fun addHour(amount: Int): CalendarCompat {
        cal[Calendar.HOUR_OF_DAY] = cal[Calendar.HOUR_OF_DAY] + amount
        return this
    }

    fun addMinute(amount: Int): CalendarCompat {
        cal[Calendar.MINUTE] = cal[Calendar.MINUTE] + amount
        return this
    }

    fun addSecond(amount: Int): CalendarCompat {
        cal[Calendar.SECOND] = cal[Calendar.SECOND] + amount
        return this
    }

    fun addMillisecond(amount: Int): CalendarCompat {
        cal[Calendar.MILLISECOND] = cal[Calendar.MILLISECOND] + amount
        return this
    }

    fun addTimeInMillis(amount: Int): CalendarCompat {
        cal.timeInMillis += amount
        return this
    }

    fun getYear(): Int {
        return cal[Calendar.YEAR]
    }

    fun getMonth(): Int {
        return cal[Calendar.MONTH] + 1
    }

    fun getDay(): Int {
        return cal[Calendar.DAY_OF_MONTH]
    }

    fun getHour(): Int {
        return cal[Calendar.HOUR_OF_DAY]
    }

    fun getMinute(): Int {
        return cal[Calendar.MINUTE]
    }

    fun getSecond(): Int {
        return cal[Calendar.SECOND]
    }

    fun getMillisecond(): Int {
        return cal[Calendar.MILLISECOND]
    }

    fun getTimeInMillis(): Long {
        return cal.timeInMillis
    }

    fun getLastDayOfMonth(): Int {
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun toDateString(format: String = "yyyyMMddHHmmss"): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        return simpleDateFormat.format(cal.timeInMillis)
    }

    fun getCalendar(): Calendar {
        return cal
    }
}
