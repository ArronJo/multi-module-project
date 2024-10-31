package com.snc.test.identification.masking

import com.snc.zero.identification.masking.Masking
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class MaskingTest : BaseJUnit5Test() {

    companion object {
        private const val MSG_MASKED_VALUE_1 = "1234567890@123ski789"
    }

    @Test
    fun `이름 - 마스킹`() {
        // given
        val testStrings = arrayOf("Normaltic", "남궁민우", "마", "허균", "홍길동")
        for (s in testStrings) {
            // when
            val v = Masking.name(s)
            // then
            logger.debug { "$s -> name -> $v" }
        }
    }

    @Test
    fun `주민등록번호 - 마스킹`() {
        // given
        val regNo = "801206-1234567"
        // when
        val v = Masking.regNo(regNo)
        // then
        logger.debug { "$regNo -> regNo -> $v" }
        assertEquals("801206-1******", v)
    }

    @Test
    fun `휴대폰번호 - 마스킹`() {
        // given
        val phoneNum = "010-1234-5678"
        // when
        val v = Masking.phoneNum(phoneNum)
        // then
        logger.debug { "$phoneNum -> phoneNum -> $v" }
        assertEquals("010-1234-****", v)
    }

    @Test
    fun `이메일 - 마스킹`() {
        // given
        val email = "abcdefg@hanwha.com"
        // when
        val v = Masking.email(email)
        // then
        logger.debug { "$email -> email -> $v" }
        assertEquals("abcd***@hanwha.com", v)
    }

    @Test
    fun `masking() 테스트 - 'Empty'`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[]")
        // then
        logger.debug { "$s -> '[*]' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }

    @Test
    fun `masking() 테스트 - '별'`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[*]")
        // then
        logger.debug { "$s -> '[*]' -> $v" }
        assertEquals("********************", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '12'-1`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "12")
        // then
        logger.debug { "$s -> '12' -> $v" }
        assertEquals("**34567890@**3ski789", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '12'-2`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[12]")
        // then
        logger.debug { "$s -> '[12]' -> $v" }
        assertEquals("1234567890@1**ski789", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 'ski'`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "ski")
        // then
        logger.debug { "$s -> 'ski' -> $v" }
        assertEquals("1234567890@123***789", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 'ski'-2`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[ski]")
        // then
        logger.debug { "$s -> '[ski]' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-7'`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[3-7]")
        // then
        logger.debug { "$s -> '[3-7]' -> $v" }
        assertEquals("123*****90@123ski789", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-77'`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[3-77]")
        // then
        logger.debug { "$s -> '[3-77]' -> $v" }
        assertEquals("123*****************", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '20-77'`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[20-77]")
        // then
        logger.debug { "$s -> '[20-77]' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-@`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[3-@]")
        // then
        logger.debug { "$s -> '[3-@]' -> $v" }
        assertEquals("123*******@123ski789", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-A`() {
        // given
        val s = "1234567890@ 123ski789"
        // when
        val v = Masking.masking(s, "[3-A]")
        // then[
        logger.debug { "$s -> '[3-A]' -> $v" }
        assertEquals("1234567890@ 123ski789", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '-7`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[-7]")
        // then
        logger.debug { "$s -> '[-7]' -> $v" }
        assertEquals("1234567890@12*******", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 '-17`() {
        // given
        val s = "1234567890@123ski7891234567890"
        // when
        val v = Masking.masking(s, "[-10]")
        // then
        logger.debug { "$s -> '[-10]' -> $v" }
        assertEquals("1234567890@123ski789**********", v)
    }

    @Test
    fun `masking() 테스트 - 패턴 Exp 1-1`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[1")
        // then
        logger.debug { "$s -> '[1' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }

    @Test
    fun `masking() 테스트 - 패턴 Exp 1-2`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[1-")
        // then
        logger.debug { "$s -> '[1-' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }

    @Test
    fun `masking() 테스트 - 패턴 Exp 2-1`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "1]")
        // then
        logger.debug { "$s -> '1]' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }

    @Test
    fun `masking() 테스트 - 패턴 Exp 2-2`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "-1]")
        // then
        logger.debug { "$s -> '-1]' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }

    @Test
    fun `masking() 테스트 - st, ed 1-1`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, 3, 7)
        // then
        logger.debug { "$s -> '(3,7)' -> $v" }
        assertEquals("123****890@123ski789", v)
    }

    @Test
    fun `masking() 테스트 - st, ed 1-2`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, 3, 7, '-')
        // then
        logger.debug { "$s -> '(3,7)' -> $v" }
        assertEquals("123----890@123ski789", v)
    }

    @Test
    fun `masking() 테스트 - regex 1-1`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[a-z]".toRegex())
        // then
        logger.debug { "$s -> '[a-z]' -> $v" }
        assertEquals("1234567890@123***789", v)
    }

    @Test
    fun `masking() 테스트 - regex 1-2`() {
        // given
        val s = MSG_MASKED_VALUE_1
        // when
        val v = Masking.masking(s, "[A-Z]".toRegex(), '-')
        // then
        logger.debug { "$s -> 'A-Z]' -> $v" }
        assertEquals(MSG_MASKED_VALUE_1, v)
    }
}
