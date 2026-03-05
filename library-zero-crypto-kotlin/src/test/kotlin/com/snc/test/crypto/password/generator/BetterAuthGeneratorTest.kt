package com.snc.test.crypto.password.generator

import com.snc.zero.crypto.password.generator.BetterAuthGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Base64

@Suppress("NonAsciiCharacters")
class BetterAuthGeneratorTest {

    @Test
    fun `generate는 지정한 byte 길이의 랜덤 바이트를 생성한다`() {
        val bytes = BetterAuthGenerator.generate()

        Assertions.assertEquals(32, bytes.size)
    }

    @Test
    fun `generate는 매번 다른 값을 생성한다`() {
        val a = BetterAuthGenerator.generate()
        val b = BetterAuthGenerator.generate()

        Assertions.assertFalse(a.contentEquals(b))
    }

    @Test
    fun `generateAsBase64는 Base64 URL safe 문자열을 반환한다`() {
        val secret = BetterAuthGenerator.generateAsBase64()

        val decoded = Base64.getUrlDecoder().decode(secret)

        Assertions.assertEquals(32, decoded.size)
    }

    @Test
    fun `generateAsBase64 길이는 43이어야 한다`() {
        val secret = BetterAuthGenerator.generateAsBase64()

        Assertions.assertEquals(43, secret.length)
    }

    @Test
    fun `generateAsBase64는 매번 다른 값을 생성한다`() {
        val a = BetterAuthGenerator.generateAsBase64()
        val b = BetterAuthGenerator.generateAsBase64()

        Assertions.assertNotEquals(a, b)
    }
}
