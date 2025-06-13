package com.snc.test.crypto.encoder.base64

import com.snc.zero.crypto.encoder.base64.Base64.decode
import com.snc.zero.crypto.encoder.base64.Base64.encode
import com.snc.zero.crypto.encoder.base64.Base64.getEncodedSize
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class Base64Test : BaseJUnit5Test() {

    @Test
    fun `Encode 테스트`() {
        val value = "HAPPY+/=~!@#\$%^&*"

        logger.debug { "TestCase 1: " }
        val e1: String = encode(value.toByteArray())
        logger.debug { "encoded: $e1, size: ${getEncodedSize(e1)}\n" }
        logger.debug { "decoded: ${String(decode(e1))}\n" }

        val d1: ByteArray = decode(e1)
        logger.debug { "UTF8: ${String(d1, 0, d1.size, Charset.forName("UTF-8"))}" }
        for (d in d1) {
            logger.debug { "byte(d): $d" }
        }
        println()
        println()

        logger.debug { "TestCase 2: " }
        val t = ByteArray(2)
        t[0] = -128
        t[1] = -23

        val e2: String = encode(t)
        val d2: ByteArray = decode(e2)
        for (b in e2.toByteArray()) {
            logger.debug { "byte(e): $b" }
        }
        for (b in d2) {
            logger.debug { "byte(d)): $b" }
        }
        println()
        println()

//        val sampleUrl = "https://www.google.com/search?q=Base64+encode"
//        println("인코딩할 문자열 : $sampleUrl")
//
//        // encoding
//        val encodingUrl = java.util.Base64.getEncoder().encodeToString(sampleUrl.toByteArray())
//        println("base64로 인코딩된 문자열: $encodingUrl")
//        // aHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS9zZWFyY2g_cT1CYXNlNjQrZW5jb2Rl
//
//        // decoding
//        val decodingText = String(java.util.Base64.getDecoder().decode(encodingUrl))
//        println("base64로 디코딩된 문자열: $decodingText")
    }

    @Test
    fun `Encode To Base64 변환 1`() {
        // given
        val data = "asdsncynh 2984yhd89`yu8989189u9jqfdasjgfuiasgds"
        // when
        val v1 = encode(data.toByteArray())
        val v2 = java.util.Base64.getEncoder().encodeToString(data.toByteArray())
        // then
        logger.debug { "Base64 encoded 1: $v1" }
        logger.debug { "Base64 encoded 2: $v2" }
        assertEquals(v2, v1)
    }

    @Test
    fun `Encode To Base64 변환 2`() {
        // given
        val data = listOf(
            //"ÿ",
            //"û",
            //"M",
            //"Ma",
            //"Man",
            "Ma\u007e",
            "Ma\u007f",
        )
        // when
        for (input in data) {
            val v = encode(input.toByteArray())
            // then
            logger.debug { "Base64 encoded : $v" }
        }
    }

    @Test
    fun `Encode To Base64 (Safe URL) 변환`() {
        // given
        val data = "Special chars: \u00fb\u00fb\u00ff here."
        // when
        val v1 = encode(data.toByteArray())
        val v2 = String(java.util.Base64.getEncoder().encode(data.toByteArray()))
        // then
        // Safe URL Base64 인코딩 처리되면서  "/" -> "_" 로 변경되므로 값이 다르게 표현된다.
        logger.debug { "Base64 encoded 1: $v1" } // "U3BlY2lhbCBjaGFyczogw7vDu8O_IGhlcmUu"
        logger.debug { "Base64 encoded 2: $v2" } // "U3BlY2lhbCBjaGFyczogw7vDu8O/IGhlcmUu"
        val nv = v1.replace("-", "+").replace("_", "/")
        assertEquals(v2, nv)
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
//
//        //assertNotEquals(v1, v2)
//    }
}
