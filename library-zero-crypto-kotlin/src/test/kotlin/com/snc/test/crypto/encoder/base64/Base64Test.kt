package com.snc.test.crypto.encoder.base64

import com.snc.zero.crypto.encoder.base64.Base64
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class Base64Test : BaseJUnit5Test() {

    @Test
    fun `Encode To Base64`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v = Base64.encode(data.toByteArray())
        val v2 = java.util.Base64.getEncoder().encodeToString(data.toByteArray())
        // then
        logger.debug { "Base64 encoded 1: $v" }
        logger.debug { "Base64 encoded 2: $v2" }
        assertEquals(v, v2)
    }

    @Test
    fun `Encode To Base64 (Safe URL)`() {
        // given
        val data = "Special chars: \u00fb\u00fb\u00ff here."
        // when
        val v = Base64.encode(data.toByteArray())
        val v2 = String(java.util.Base64.getEncoder().encode(data.toByteArray()))
        // then
        // Safe URL Base64 인코딩 처리되면서  "/" -> "_" 로 변경되므로 값이 다르게 표현된다.
        logger.debug { "Base64 encoded 1: $v" }  // "U3BlY2lhbCBjaGFyczogw7vDu8O_IGhlcmUu"
        logger.debug { "Base64 encoded 2: $v2" } // "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"
        assertNotEquals(v, v2)
    }

//    @Test
//    fun `'Fully covered by tests' 를 받기 위해 uncovered line 테스트 함수 구현 1`() {
//        // given
//        // when
//        val v1 = Base64.getByte('a')
//        val v2 = Base64.getByte('+')
//        val v3 = Base64.getByte('-')
//        val v4 = Base64.getByte('/')
//        val v5 = Base64.getByte('_')
//        val v6 = Base64.getByte('*')
//        // then
//        logger.debug { "Base64 getByte('a'): $v1" }
//        logger.debug { "Base64 getByte('+'): $v2" }
//        logger.debug { "Base64 getByte('-'): $v3" }
//        logger.debug { "Base64 getByte('/'): $v4" }
//        logger.debug { "Base64 getByte('_'): $v5" }
//        logger.debug { "Base64 getByte('*'): $v6" }
//        //assertNotEquals(v, v2)
//    }
}