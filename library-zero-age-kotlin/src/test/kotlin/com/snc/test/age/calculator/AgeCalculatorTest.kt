package com.snc.test.age.calculator

import com.snc.zero.age.calculator.AgeCalculator
import com.snc.zero.extensions.text.padStart
import com.snc.zero.extensions.text.print
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
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
        val insAge = AgeCalculator.calculateInsAge(birthDate, currentDate)
        // then
        logger.debug { "보험나이: $insAge 세" }
    }

    @Test
    fun `통합 계산`() {
        // given
        val birthDate = "$yearOfBirth${monthOfBirth.padStart(2, '0')}${dayOfBirth.padStart(2, '0')}" // 생년월일 예시
        val currentDate =
            "$yearOfTarget${monthOfTarget.padStart(2, '0')}${dayOfTarget.padStart(2, '0')}" //LocalDate.now() // 현재 날짜
        // when
        val age = AgeCalculator.calculateManInsAge(birthDate, currentDate)
        // then
        logger.debug { "통합 계산: ${age.print()}" }
    }

    @Test
    fun `통합 계산 ChatGPT`() {
        // given
        // when
        val age = AgeCalculator.calculateManInsAge(
            yearOfBirth,
            monthOfBirth,
            dayOfBirth,
            yearOfTarget,
            monthOfTarget,
            dayOfTarget
        )
        // then
        logger.debug { "통합 계산 ChatGPT ${age.print()}" }
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

    @Test
    fun `calculateManAge - 생일 지나지 않았을 경우`() {
        val birthDate = LocalDate.of(2000, 6, 15)
        val targetDate = LocalDate.of(2020, 6, 1)
        val age = AgeCalculator.calculateManAge(birthDate, targetDate)
        assertEquals(19, age)
    }

    @Test
    fun `calculateManAge - 생일 지난 경우`() {
        val birthDate = LocalDate.of(2000, 6, 1)
        val targetDate = LocalDate.of(2020, 6, 15)
        val age = AgeCalculator.calculateManAge(birthDate, targetDate)
        assertEquals(20, age)
    }

    @Test
    fun `calculateInsAge - 생일로부터 6개월 전`() {
        val birthDate = LocalDate.of(2000, 1, 1)
        val targetDate = LocalDate.of(2020, 6, 30)
        val age = AgeCalculator.calculateInsAge(birthDate, targetDate)
        assertEquals(20, age)
    }

    @Test
    fun `calculateInsAge - 생일로부터 6개월 지남`() {
        val birthDate = LocalDate.of(2000, 1, 1)
        val targetDate = LocalDate.of(2020, 7, 2)
        val age = AgeCalculator.calculateInsAge(birthDate, targetDate)
        assertEquals(21, age)
    }

    @Test
    fun `calculateManInsAge(Int) - 정상 입력`() {
        val result = AgeCalculator.calculateManInsAge(1990, 5, 20, 2020, 6, 20)
        assertAll(
            { assertEquals("30", result[0]) }, // manAge
            { assertEquals("30", result[1]) }, // insAge
            { assertEquals(8, result[2].length) }, // seniorityDate
            { assertEquals(8, result[3].length) }, // targetDate
            { assertTrue(result[4].toLong() > 0L) } // difference
        )
    }

    @Test
    fun `calculateManInsAge(Int) - 존재하지 않는 날짜`() {
        val result = AgeCalculator.calculateManInsAge(1990, 2, 30, 2020, 6, 20)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `calculateManInsAge(String) - 정상 입력`() {
        val result = AgeCalculator.calculateManInsAge("19900520", "20200620")
        assertArrayEquals(arrayOf(30, 30), result)
    }

    @Test
    fun `calculateManInsAge(String) - 생일 전`() {
        val result = AgeCalculator.calculateManInsAge("19900520", "20200519")
        assertArrayEquals(arrayOf(29, 30), result)
    }

    @Test
    fun `calculateManInsAge(String) - 잘못된 입력 길이`() {
        val result = AgeCalculator.calculateManInsAge("1990520", "20200620")
        assertTrue(result.isEmpty())
    }

    @Nested
    inner class AgeCalculatorDeprecatedMethodTest {

        @Test
        fun `calculateManInsAge with birthDate length not 8 returns empty array`() {
            val result = AgeCalculator.calculateManInsAge("199001", "20250101")
            assertArrayEquals(arrayOf<Int>(), result)
        }

        @Test
        fun `calculateManInsAge with targetDate length not 8 returns empty array`() {
            val result = AgeCalculator.calculateManInsAge("19900101", "20251")
            assertArrayEquals(arrayOf<Int>(), result)
        }

        @Test
        fun `calculateManInsAge with correct date length returns expected result`() {
            val result = AgeCalculator.calculateManInsAge("19900101", "20250101")
            assertEquals(35, result[0])
            assertEquals(35, result[1]) // 6개월 안 지났으므로 보험나이 그대로
        }
    }

    @Nested
    inner class AgeCalculatorManInsAgeTest {

        @Test
        fun `targetDate equals seniorityDate increments insAge`() {
            val result = AgeCalculator.calculateManInsAge(
                2000,
                1,
                1,
                2020,
                7,
                1 // 생일 + 20년 + 6개월
            )
            assertEquals("20", result[0]) // 만 나이
            assertEquals("21", result[1]) // 보험나이 증가
        }

        @Test
        fun `targetDate after seniorityDate increments insAge`() {
            val result = AgeCalculator.calculateManInsAge(
                2000,
                1,
                1,
                2020,
                8,
                1 // 생일 + 20년 + 7개월
            )
            assertEquals("20", result[0])
            assertEquals("21", result[1])
        }

        @Test
        fun `targetDate before seniorityDate does not increment insAge`() {
            val result = AgeCalculator.calculateManInsAge(
                2000,
                1,
                1,
                2020,
                6,
                30 // 생일 + 20년 + 5개월 29일
            )
            assertEquals("20", result[0])
            assertEquals("20", result[1]) // 보험나이 그대로
        }
    }
}
