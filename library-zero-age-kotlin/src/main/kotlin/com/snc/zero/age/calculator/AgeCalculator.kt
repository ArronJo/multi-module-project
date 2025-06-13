package com.snc.zero.age.calculator

import com.snc.zero.extension.calendar.diff
import com.snc.zero.extension.format.formatDateTime
import com.snc.zero.logger.jvm.TLogging
import java.time.LocalDate
import java.time.Period
import java.util.Calendar

private val logger = TLogging.logger { }

/**
 * 나이 계산
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class AgeCalculator private constructor() {

    companion object {

        fun calculateManAge(birthDate: LocalDate, targetDate: LocalDate): Int {
            return Period.between(birthDate, targetDate).years
        }

        fun calculateInsAge(birthDate: LocalDate, targetDate: LocalDate): Int {
            var age = Period.between(birthDate, targetDate).years
            // 생일로부터 6개월이 지났는지 확인
            val halfYearAfterBirthday = birthDate.plusYears(age.toLong()).plusMonths(6)

            if (!targetDate.isBefore(halfYearAfterBirthday)) {
                // 생일 후 6개월이 지났다면, 나이를 1 증가
                age++
            }
            return age
        }

        /**
         * 추천
         */
        fun calculateManInsAge(
            yearOfBirth: Int,
            monthOfBirth: Int,
            dayOfBirth: Int,
            yearOfTarget: Int,
            monthOfTarget: Int,
            dayOfTarget: Int,
        ): Array<String> {
            val birthDate = Calendar.getInstance()
            val targetDate = Calendar.getInstance()
            birthDate[yearOfBirth, monthOfBirth - 1] = 1
            if (birthDate.getActualMaximum(Calendar.DAY_OF_MONTH) < dayOfBirth) {
                logger.warn { "Exception: Invalid Date = $yearOfBirth-$monthOfBirth-$dayOfBirth" }
                return arrayOf()
            }
            birthDate[yearOfBirth, monthOfBirth - 1] = dayOfBirth
            targetDate[yearOfTarget, monthOfTarget - 1] = dayOfTarget

            var manAge = targetDate[Calendar.YEAR] - birthDate[Calendar.YEAR]
            if (targetDate[Calendar.DAY_OF_YEAR] <= birthDate[Calendar.DAY_OF_YEAR]) {
                manAge--
            }

            var insAge = manAge

            val seniorityDate = birthDate.clone() as Calendar
            seniorityDate.add(Calendar.YEAR, manAge)
            seniorityDate[Calendar.MONTH] = birthDate[Calendar.MONTH]
            seniorityDate.add(Calendar.MONTH, 6)
            seniorityDate[Calendar.DATE] = birthDate[Calendar.DATE]
            if (targetDate == seniorityDate || targetDate.after(seniorityDate)) {
                insAge++
                seniorityDate.add(Calendar.YEAR, 1)
            }
            val differenceInDays: Long = targetDate.diff(seniorityDate)

            return arrayOf(
                manAge.toString(),
                insAge.toString(),
                seniorityDate.time.formatDateTime("yyyyMMdd"),
                targetDate.time.formatDateTime("yyyyMMdd"),
                differenceInDays.toString()
            )
        }

        /**
         * 비추천
         * @Deprecated("calculateManInsAge(Int, Int, Int, Int, Int, Int) instead")
         */
        fun calculateManInsAge(birthDate: String, targetDate: String): Array<Int> {
            if (birthDate.length != 8 || targetDate.length != 8) {
                return arrayOf()
            }
            var stYYYY = targetDate.substring(0, 4).toInt()
            var stMM = targetDate.substring(4, 6).toInt()
            val stDD = targetDate.substring(6, 8).toInt()

            val birthYYYY = birthDate.substring(0, 4).toInt()
            val birthMM = birthDate.substring(4, 6).toInt()
            val birthDD = birthDate.substring(6, 8).toInt()

            if (stDD < birthDD) {
                stMM -= 1
            }
            if (stMM < birthMM) {
                stMM += 12
                stYYYY -= 1
            }
            val manAge = stYYYY - birthYYYY
            var insuredAge = manAge
            if (stMM - birthMM >= 6) {
                insuredAge = manAge + 1
            }
            return arrayOf(manAge, insuredAge)
        }
    }
}
