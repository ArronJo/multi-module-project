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
        logger.debug { PatternMasking.id("900101-1234567") }  // 출력: 900101-1******
        logger.debug { PatternMasking.id("9001011234567") }   // 출력: 900101-1******
        logger.debug { PatternMasking.id("HelloWorld") }      // 출력: HelloWorld (변경 없음)
    }

    @Test
    fun `패턴 마스킹 - 전화번호`() {
        logger.debug { PatternMasking.phoneNum("01012345678") }  // 출력: 010-****-5678
        logger.debug { PatternMasking.phoneNum("010-1234-5678") }  // 출력: 010-****-5678
        logger.debug { PatternMasking.phoneNum("016-123-4567") }  // 출력: 016-****-4567
        logger.debug { PatternMasking.phoneNum("HelloWorld") }  // 출력: HelloWorld (변경 없음)
    }

    @Test
    fun `패턴 마스킹 - 계좌번호`() {
        logger.debug { PatternMasking.account("123456-78-901234") }  // 출력: 12****-**-****34
        logger.debug { PatternMasking.account("1234567890123") }     // 출력: 12****-****-***23
        logger.debug { PatternMasking.account("12-3456-7890") }      // 출력: 12-****-**90
        logger.debug { PatternMasking.account("HelloWorld") }        // 출력: HelloWorld (변경 없음)
    }

    @Test
    fun `패턴 마스킹 - 카드`() {
        logger.debug { PatternMasking.card("1234-5678-9012-3456") }  // 출력: 1234-****-****-3456
        logger.debug { PatternMasking.card("1234567890123456") }     // 출력: 1234-****-****-3456
        logger.debug { PatternMasking.card("1234 567890 12345") }    // 출력: 1234-******-12345 (American Express)
        logger.debug { PatternMasking.card("HelloWorld") }           // 출력: HelloWorld (변경 없음)
    }

    @Test
    fun `패턴 마스킹 - 주소 상세주소`() {
        logger.debug { PatternMasking.addressDetail("서울특별시 강남구 테헤란로 123 길동빌딩 5층") } // 출력: 서울특별시 강남구 테헤란로 123 길동**** 5*
        logger.debug { PatternMasking.addressDetail("경기도 성남시 분당구 판교동 123-45 판교아파트 101동 1001호") } // 출력: 경기도 성남시 분당구 판교동 123-45 판교***** 10***
        logger.debug { PatternMasking.addressDetail("제주특별자치도 제주시 연동 1234") } // 출력: 제주특별자치도 제주시 연동 1234
        logger.debug { PatternMasking.addressDetail("서울시 송파구 잠실동 123-456 롯데타워 123층") } // 출력: 서울시 송파구 잠실동 123-456 롯데*** 12**
        logger.debug { PatternMasking.addressDetail("서울시 강남구 역삼동 123-45 A동") } // 출력: 서울시 강남구 역삼동 123-45 A*
        logger.debug { PatternMasking.addressDetail("A동 1004호") } // 출력: A* 10**
    }
}