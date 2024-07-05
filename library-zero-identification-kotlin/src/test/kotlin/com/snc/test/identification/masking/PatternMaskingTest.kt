package com.snc.test.identification.masking

import com.snc.zero.identification.masking.PatternMasking
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class PatternMaskingTest : BaseJUnit5Test() {

    @Test
    fun `패턴 마스킹 - 주민등록번호`() {
        val v1 = PatternMasking.id("900101-1234567")
        logger.debug { v1 }  // 출력: 900101-1******
        assertEquals("900101-1******", v1)

        val v2 = PatternMasking.id("9001011234567")
        logger.debug { v2 }   // 출력: 900101-1******
        assertEquals("900101-1******", v2)

        val v3 = PatternMasking.id("HelloWorld")
        logger.debug { v3 }      // 출력: HelloWorld (변경 없음)
        assertEquals("HelloWorld", v3)

        val v4 = PatternMasking.id("")
        logger.debug { v4 }      // 출력: HelloWorld (변경 없음)
        assertEquals("", v4)
    }

    @Test
    fun `패턴 마스킹 - 전화번호`() {
        val v1 = PatternMasking.phoneNum("01012345678")
        logger.debug { v1 }  // 출력: 010-****-5678
        assertEquals("010-****-5678", v1)

        val v2 = PatternMasking.phoneNum("010-1234-5678")
        logger.debug { v2 }  // 출력: 010-****-5678
        assertEquals("010-****-5678", v2)

        val v3 = PatternMasking.phoneNum("016-123-4567")
        logger.debug { v3 }  // 출력: 016-****-4567
        assertEquals("016-****-4567", v3)

        val v4 = PatternMasking.phoneNum("HelloWorld")
        logger.debug { v4 }  // 출력: HelloWorld (변경 없음)
        assertEquals("HelloWorld", v4)
    }

    @Test
    fun `패턴 마스킹 - 계좌번호`() {
        val v1 = PatternMasking.account("123456-78-901234")
        logger.debug { v1 }  // 출력: 12****-**-****34
        assertEquals("12****-**-****34", v1)

        val v2 = PatternMasking.account("1234567890123")
        logger.debug { v2 }     // 출력: 12****-*****-23
        assertEquals("12****-*****-23", v2)

        val v3 = PatternMasking.account("12-3456-7890")
        logger.debug { v3 }      // 출력: 12-****-**90
        assertEquals("12-****-**90", v3)

        val v4 = PatternMasking.account("HelloWorld")
        logger.debug { v4 }       // 출력: HelloWorld (변경 없음)
        assertEquals("HelloWorld", v4)
    }

    @Test
    fun `패턴 마스킹 - 카드`() {
        val v1 = PatternMasking.card("1234-5678-9012-3456")
        logger.debug { v1 }  // 출력: 1234-****-****-3456
        assertEquals("1234-****-****-3456", v1)

        val v2 = PatternMasking.card("1234567890123456")
        logger.debug { v2 }     // 출력: 1234-****-****-3456
        assertEquals("1234-****-****-3456", v2)

        val v3 = PatternMasking.card("1234 567890 12345")
        logger.debug { v3 }      // 출력: 1234-******-12345 (American Express)
        assertEquals("1234-******-12345", v3)

        val v4 = PatternMasking.card("HelloWorld")
        logger.debug { v4 }       // 출력: HelloWorld (변경 없음)
        assertEquals("HelloWorld", v4)
    }

    @Test
    fun `패턴 마스킹 - 주소 상세주소`() {
        val v1 = PatternMasking.addressDetail("서울특별시 강남구 테헤란로 123 길동빌딩 5층")
        logger.debug { v1 }  // 출력: 서울특별시 강남구 테헤란로 123 길동**** 5*
        assertEquals("서울특별시 강남구 테헤란로 123 길*** 5층", v1)

        val v2 = PatternMasking.addressDetail("경기도 성남시 분당구 판교동 123-45 판교아파트 101동 1001호")
        logger.debug { v2 }     // 출력: 경기도 성남시 분당구 판교동 123-45 판교아파트 1*** 1****
        assertEquals("경기도 성남시 분당구 판교동 123-45 판교아파트 1*** 1****", v2)

        val v3 = PatternMasking.addressDetail("제주특별자치도 제주시 연동 1234")
        logger.debug { v3 }      // 출력: 제주특별자치도 제주시 연동 1***
        assertEquals("제주특별자치도 제주시 연동 1***", v3)

        val v4 = PatternMasking.addressDetail("서울시 송파구 잠실동 123-456 롯데타워 123층")
        logger.debug { v4 }       // 출력: 서울시 송파구 잠실동 123-456 롯*** 1***
        assertEquals("서울시 송파구 잠실동 123-456 롯*** 1***", v4)

        val v5 = PatternMasking.addressDetail("서울시 강남구 역삼동 123-45 A동")
        logger.debug { v5 }       // 출력: 서울시 강남구 역삼동 12**** A동
        assertEquals("서울시 강남구 역삼동 12**** A동", v5)

        val v6 = PatternMasking.addressDetail("A동 1004호")
        logger.debug { v6 }       // 출력: A* 10**
        assertEquals("A동 1****", v6)
    }
}