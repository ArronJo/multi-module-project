package com.snc.test.age.calculator

import com.snc.zero.age.calculator.AgeCalculator
import com.snc.zero.core.extensions.text.padStart
import com.snc.zero.core.extensions.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.time.LocalDate

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class AgeCalculatorTest : BaseJUnit5Test() {

    private var yearOfBirth: Int = 0
    private var monthOfBirth: Int = 0
    private var dayOfBirth: Int = 0
    private var yearOfTarget: Int = 0
    private var monthOfTarget: Int = 0
    private var dayOfTarget: Int = 0

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        yearOfBirth = 1980
        monthOfBirth = 4
        dayOfBirth = 9
        yearOfTarget = 2024
        monthOfTarget = 4
        dayOfTarget = 8
    }

    @Test
    fun `만나이 계산`() {
        // given
        val birthDate = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth) // 생년월일 예시
        val currentDate = LocalDate.of(yearOfTarget, monthOfTarget, dayOfTarget)//LocalDate.now() // 현재 날짜
        // when
        val manAge = AgeCalculator.calculateManAge(birthDate, currentDate)
        // then
        logger.debug { "만나이: $manAge 세" }
    }

    @Test
    fun `보험나이 계산`() {
        // given
        val birthDate = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth) // 생년월일 예시
        val currentDate = LocalDate.of(yearOfTarget, monthOfTarget, dayOfTarget)//LocalDate.now() // 현재 날짜
        // when
        val manAge = AgeCalculator.calculateInsAge(birthDate, currentDate)
        // then
        logger.debug { "보험나이: $manAge 세" }
    }

    @Test
    fun `통합 계산`() {
        // given
        val birthDate = "$yearOfBirth${monthOfBirth.padStart(2, '0')}${dayOfBirth.padStart(2, '0')}" // 생년월일 예시
        val currentDate =
            "$yearOfTarget${monthOfTarget.padStart(2, '0')}${dayOfTarget.padStart(2, '0')}" //LocalDate.now() // 현재 날짜
        // when
        val manAge = AgeCalculator.calculateManInsAge(birthDate, currentDate)
        // then
        logger.debug { "통합 계산: ${manAge.print()}" }
    }

    @Test
    fun `통합 계산 ChatGPT`() {
        // given
        // when
        val manAge = AgeCalculator.calculateManInsAge(
            yearOfBirth,
            monthOfBirth,
            dayOfBirth,
            yearOfTarget,
            monthOfTarget,
            dayOfTarget
        )
        // then
        logger.debug { "통합 계산 ChatGPT ${manAge.print()}" }
    }

    @Test
    fun `calculateManInsAge TDD 지키자`() {
        AgeCalculator.calculateManInsAge("123456", "123456")
        AgeCalculator.calculateManInsAge("701205", "700101")

        AgeCalculator.calculateInsAge(
            LocalDate.of(1970, 1, 1),
            LocalDate.of(2024, 8, 8)
        )
        AgeCalculator.calculateManInsAge(1978, 1, 1, 1970, 8, 8)
    }
}
