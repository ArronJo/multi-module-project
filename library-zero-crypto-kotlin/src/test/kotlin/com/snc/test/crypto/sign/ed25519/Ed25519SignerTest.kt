package com.snc.test.crypto.sign.ed25519

import com.snc.test.crypto.sign.ed25519.message.TestMessageFactory
import com.snc.zero.crypto.sign.ed25519.Ed25519Signer
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.security.KeyPair

@Suppress("NonAsciiCharacters")
@DisplayName("Ed25519T 테스트")
class Ed25519SignerTest {

    private lateinit var keyPair: KeyPair

    @BeforeEach
    fun setup() {
        keyPair = Ed25519Signer.generateKeyPair()
    }

    @Nested
    @DisplayName("기본 테스트")
    inner class DefaultTest {

        @Test
        @DisplayName("기본 2048비트 키 페어 생성")
        fun `전자서명 알고리즘 테스트`() {
            val message = "Hello Ed25519".toByteArray()

            val signature = Ed25519Signer.sign(
                keyPair.private,
                message
            )

            val isValid = Ed25519Signer.verify(
                keyPair.public,
                message,
                signature
            )

            println("Signature valid: $isValid")
            assertTrue(isValid)
        }
    }

    @Nested
    @DisplayName("전자서명 테스트")
    inner class SignTest {

        @Test
        fun `HMAC 포함 메시지 서명 및 검증 성공`() {
            // given
            val message = TestMessageFactory.create()

            // when
            val signature = Ed25519Signer.sign(
                keyPair.private,
                message
            )

            val result = Ed25519Signer.verify(
                keyPair.public,
                message,
                signature
            )

            // then
            assertTrue(result)
        }

        @Test
        fun `메시지 변조 시 검증 실패`() {
            // given
            val original = TestMessageFactory.create()

            val tampered = String(original)
                .replace("4500", "9000")
                .toByteArray()

            val signature = Ed25519Signer.sign(
                keyPair.private,
                original
            )

            // when
            val result = Ed25519Signer.verify(
                keyPair.public,
                tampered,
                signature
            )

            // then
            assertFalse(result)
        }

        @Test
        fun `서명 위조 시 검증 실패`() {
            // given
            val message = TestMessageFactory.create()

            val signature = Ed25519Signer.sign(
                keyPair.private,
                message
            )

            // 서명 조작
            signature[0] = (signature[0] + 1).toByte()

            // when
            val result = Ed25519Signer.verify(
                keyPair.public,
                message,
                signature
            )

            // then
            assertFalse(result)
        }

        @Test
        fun `다른 키로는 검증 실패`() {
            // given
            val otherKeyPair = Ed25519Signer.generateKeyPair()

            val message = TestMessageFactory.create()

            val signature = Ed25519Signer.sign(
                keyPair.private,
                message
            )

            // when
            val result = Ed25519Signer.verify(
                otherKeyPair.public,
                message,
                signature
            )

            // then
            assertFalse(result)
        }
    }
}
