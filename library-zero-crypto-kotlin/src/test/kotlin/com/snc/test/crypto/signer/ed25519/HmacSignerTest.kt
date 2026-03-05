package com.snc.test.crypto.signer.ed25519

import com.snc.zero.crypto.password.generator.BetterAuthGenerator
import com.snc.zero.crypto.signer.ed25519.HmacSigner
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
@DisplayName("HmacSigner 테스트")
class HmacSignerTest {

    private lateinit var secretKey: ByteArray
    private lateinit var hmacSigner: HmacSigner

    @BeforeEach
    fun setup() {
        secretKey = BetterAuthGenerator.generate()
        hmacSigner = HmacSigner(secretKey)
    }

    @Nested
    @DisplayName("HMAC 테스트")
    inner class HmacTest {

        @Test
        fun `동일 메시지와 키로 HMAC 서명 일치`() {
            // given
            val message = "amount=4500&currency=KRW"

            // when
            val hmac1 = hmacSigner.sign(message)
            val hmac2 = hmacSigner.sign(message)

            // then
            assertTrue(hmac1 == hmac2)
        }

        @Test
        fun `HMAC verify 성공`() {
            // given
            val message = "amount=4500&currency=KRW"
            val hmac = hmacSigner.sign(message)

            // when
            val result = hmacSigner.verify(message, hmac)

            // then
            assertTrue(result)
        }

        @Test
        fun `메시지 변조 시 HMAC verify 실패`() {
            // given
            val original = "amount=4500&currency=KRW"
            val hmac = hmacSigner.sign(original)

            // when
            val result = hmacSigner.verify("amount=9000&currency=KRW", hmac)

            // then
            assertFalse(result)
        }

        @Test
        fun `다른 키로 생성한 HMAC은 검증 실패`() {
            // given
            val message = "amount=4500&currency=KRW"
            val otherHmacSigner = HmacSigner("other-secret-key".toByteArray(Charsets.UTF_8))

            val hmac = hmacSigner.sign(message)

            // when
            val result = otherHmacSigner.verify(message, hmac)

            // then
            assertFalse(result)
        }
    }
}
