package com.snc.test.identification.masking

import com.snc.zero.identification.masking.Masking
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class MaskingTest : BaseJUnit5Test() {

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
        assertEquals(v, "801206-1******")
    }

    @Test
    fun `휴대폰번호 - 마스킹`() {
        // given
        val phoneNum = "010-1234-5678"
        // when
        val v = Masking.phoneNum(phoneNum)
        // then
        logger.debug { "$phoneNum -> phoneNum -> $v" }
        assertEquals(v, "010-1234-****")
    }

    @Test
    fun `이메일 - 마스킹`() {
        // given
        val email = "abcdefg@hanwha.com"
        // when
        val v = Masking.email(email)
        // then
        logger.debug { "$email -> email -> $v" }
        assertEquals(v, "abcd***@hanwha.com")
    }

    @Test
    fun `masking() 테스트 - '별'`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "[*]")
        // then
        logger.debug { "$s -> '[*]' -> $v" }
        assertEquals(v, "********************")
    }

    @Test
    fun `masking() 테스트 - 패턴 '12'`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "12")
        // then
        logger.debug { "$s -> '12' -> $v" }
        //assertEquals(v, "1234567890@123ski789")
    }

    @Test
    fun `masking() 테스트 - 패턴 'ski'`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "ski")
        // then
        logger.debug { "$s -> 'ski' -> $v" }
        assertEquals(v, "1234567890@123***789")
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-7'`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "[3-7]")
        // then
        logger.debug { "$s -> '[3-7]' -> $v" }
        assertEquals(v, "123*****90@123ski789")
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-77'`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "[3-77]")
        // then
        logger.debug { "$s -> '[3-77]' -> $v" }
        assertEquals(v, "123*****************")
    }

    @Test
    fun `masking() 테스트 - 패턴 '20-77'`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "[20-77]")
        // then
        logger.debug { "$s -> '[20-77]' -> $v" }
        assertEquals(v, "1234567890@123ski789")
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-@`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "[3-@]")
        // then
        logger.debug { "$s -> '[3-@]' -> $v" }
        assertEquals(v, "123*******@123ski789")
    }

    @Test
    fun `masking() 테스트 - 패턴 '3-A`() {
        // given
        val s = "1234567890@ 123ski789"
        // when
        val v = Masking.masking(s, "[3-A]")
        // then[
        logger.debug { "$s -> '[3-A]' -> $v" }
        assertEquals(v, "1234567890@ 123ski789")
    }

    @Test
    fun `masking() 테스트 - 패턴 '-7`() {
        // given
        val s = "1234567890@123ski789"
        // when
        val v = Masking.masking(s, "[-7]")
        // then
        logger.debug { "$s -> '[-7]' -> $v" }
        assertEquals(v, "1234567890@12*******")
    }
}