package com.snc.test.identification.mask

import com.snc.zero.identification.mask.Mask
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class MaskTest : BaseJUnit5Test() {

    companion object {
        private const val MSG_MASKED_VALUE_1 = "1234567890@123ski789"
    }

    @Nested
    inner class TypeMask {

        @Test
        fun `이름 - 마스킹`() {
            // given
            val testStrings = arrayOf("Normaltic", "남궁민우", "마", "허균", "홍길동")
            for (s in testStrings) {
                // when
                val v = Mask.name(s)
                // then
                logger.debug { "$s -> name -> $v" }
            }
        }

        @Test
        fun `주민등록번호 - 마스킹`() {
            // given
            val regNo = "801206-1234567"
            // when
            val v = Mask.regNo(regNo)
            // then
            logger.debug { "$regNo -> regNo -> $v" }
            assertEquals("801206-1******", v)
        }

        @Test
        fun `휴대폰번호 - 마스킹`() {
            // given
            val phoneNum = "010-1234-5678"
            // when
            val v = Mask.phoneNum(phoneNum)
            // then
            logger.debug { "$phoneNum -> phoneNum -> $v" }
            assertEquals("010-1234-****", v)
        }

        @Test
        fun `이메일 - 마스킹`() {
            // given
            val email = "abcdefg@hanwha.com"
            // when
            val v = Mask.email(email)
            // then
            logger.debug { "$email -> email -> $v" }
            assertEquals("abcd***@hanwha.com", v)
        }

        @Test
        fun `주소 - 마스킹`() {
            val address = "서울 영등포구 여의동로 213 여의도 금고리첸시아 아파트 101동 101호"
            val maskedAddress = Mask.address(address)

            println("원본: $address")
            println("마스킹 후: $maskedAddress")

            // 추가 테스트 케이스
            val testCases = listOf(
                "서울 강남구 테헤란로 152",
                "부산 해운대구 우동",
                "대전 유성구 대학로 291 한국과학기술원",
                "인천 중구 공항로 272 인천국제공항",
                "서울 종로구"
            )

            println("\n=== 테스트 케이스 ===")
            testCases.forEach { testAddress ->
                println("원본: $testAddress")
                println("마스킹 후: ${Mask.address(testAddress)}")
                println()
            }
        }
    }

    @Nested
    inner class DefaultMask {

        @Test
        fun `mask() 테스트 - 'Empty'`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[]")
            // then
            logger.debug { "$s -> '[*]' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }

        @Test
        fun `mask() 테스트 - '별'`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[*]")
            // then
            logger.debug { "$s -> '[*]' -> $v" }
            assertEquals("********************", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '12'-1`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "12")
            // then
            logger.debug { "$s -> '12' -> $v" }
            assertEquals("**34567890@**3ski789", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '12'-2`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[12]")
            // then
            logger.debug { "$s -> '[12]' -> $v" }
            assertEquals("1234567890@1**ski789", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 'ski'`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "ski")
            // then
            logger.debug { "$s -> 'ski' -> $v" }
            assertEquals("1234567890@123***789", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 'ski'-2`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[ski]")
            // then
            logger.debug { "$s -> '[ski]' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '3-7'`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[3-7]")
            // then
            logger.debug { "$s -> '[3-7]' -> $v" }
            assertEquals("123*****90@123ski789", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '3-77'`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[3-77]")
            // then
            logger.debug { "$s -> '[3-77]' -> $v" }
            assertEquals("123*****************", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '20-77'`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[20-77]")
            // then
            logger.debug { "$s -> '[20-77]' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '3-@`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[3-@]")
            // then
            logger.debug { "$s -> '[3-@]' -> $v" }
            assertEquals("123*******@123ski789", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '3-A`() {
            // given
            val s = "1234567890@ 123ski789"
            // when
            val v = Mask.mask(s, "[3-A]")
            // then[
            logger.debug { "$s -> '[3-A]' -> $v" }
            assertEquals("1234567890@ 123ski789", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '-7`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[-7]")
            // then
            logger.debug { "$s -> '[-7]' -> $v" }
            assertEquals("1234567890@12*******", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 '-17`() {
            // given
            val s = "1234567890@123ski7891234567890"
            // when
            val v = Mask.mask(s, "[-10]")
            // then
            logger.debug { "$s -> '[-10]' -> $v" }
            assertEquals("1234567890@123ski789**********", v)
        }

        @Test
        fun `mask() 테스트 - 패턴 Exp 1-1`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[1")
            // then
            logger.debug { "$s -> '[1' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }

        @Test
        fun `mask() 테스트 - 패턴 Exp 1-2`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[1-")
            // then
            logger.debug { "$s -> '[1-' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }

        @Test
        fun `mask() 테스트 - 패턴 Exp 2-1`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "1]")
            // then
            logger.debug { "$s -> '1]' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }

        @Test
        fun `mask() 테스트 - 패턴 Exp 2-2`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "-1]")
            // then
            logger.debug { "$s -> '-1]' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }

        @Test
        fun `mask() 테스트 - st, ed 1-1`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, 3, 7)
            // then
            logger.debug { "$s -> '(3,7)' -> $v" }
            assertEquals("123****890@123ski789", v)
        }

        @Test
        fun `mask() 테스트 - st, ed 1-2`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, 3, 7, '-')
            // then
            logger.debug { "$s -> '(3,7)' -> $v" }
            assertEquals("123----890@123ski789", v)
        }

        @Test
        fun `mask() 테스트 - regex 1-1`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[a-z]".toRegex())
            // then
            logger.debug { "$s -> '[a-z]' -> $v" }
            assertEquals("1234567890@123***789", v)
        }

        @Test
        fun `mask() 테스트 - regex 1-2`() {
            // given
            val s = MSG_MASKED_VALUE_1
            // when
            val v = Mask.mask(s, "[A-Z]".toRegex(), '-')
            // then
            logger.debug { "$s -> 'A-Z]' -> $v" }
            assertEquals(MSG_MASKED_VALUE_1, v)
        }
    }
}
