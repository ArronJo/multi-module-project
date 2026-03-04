package com.snc.test.crypto.signer.ed25519

import com.snc.test.crypto.signer.ed25519.message.TestMessageFactory
import com.snc.zero.crypto.key.generator.BetterAuthGenerator
import com.snc.zero.crypto.signer.ed25519.Ed25519Signer
import com.snc.zero.crypto.signer.ed25519.JsonHashVerifier
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.security.KeyPair
import java.security.SignatureException

@Suppress("NonAsciiCharacters")
@DisplayName("Ed25519Signer 테스트")
class Ed25519SignerTest {

    private lateinit var signer: Ed25519Signer
    private lateinit var keyPair: KeyPair
    private lateinit var secretKey: ByteArray

    @BeforeEach
    fun setup() {
        signer = Ed25519Signer()
        keyPair = signer.generateKeyPair()
        secretKey = BetterAuthGenerator.generate()
    }

    @Nested
    @DisplayName("기본 테스트")
    inner class DefaultTest {

        @Test
        @DisplayName("Ed25519 키 페어 생성 및 기본 서명 검증")
        fun `전자서명 알고리즘 테스트`() {
            val message = "Hello Ed25519".toByteArray()

            val signature = signer.sign(keyPair.private, message)
            val isValid = signer.verify(keyPair.public, message, signature)

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
            val message = TestMessageFactory.create(secretKey)

            // when
            val signature = signer.sign(keyPair.private, message)
            val result = signer.verify(keyPair.public, message, signature)

            // then
            assertTrue(result)
        }

        @Test
        fun `메시지 변조 시 검증 실패`() {
            // given
            val original = TestMessageFactory.create(secretKey)
            val tampered = String(original).replace("4500", "9000").toByteArray()
            val signature = signer.sign(keyPair.private, original)

            // when
            val result = signer.verify(keyPair.public, tampered, signature)

            // then
            assertFalse(result)
        }

        @Test
        fun `서명 위조 시 검증 실패`() {
            try {
                // given
                val message = TestMessageFactory.create(secretKey)
                val signature = signer.sign(keyPair.private, message)

                // 서명 조작
                signature[0] = (signature[0] + 1).toByte()

                // when
                val result = signer.verify(keyPair.public, message, signature)

                // then
                assertFalse(result)

            } catch (e: SignatureException) {
                println("Signature verification failed as expected: $e")
            }
        }

        @Test
        fun `다른 키로는 검증 실패`() {
            // given
            val otherKeyPair = signer.generateKeyPair()
            val message = TestMessageFactory.create(secretKey)
            val signature = signer.sign(keyPair.private, message)

            // when
            val result = signer.verify(otherKeyPair.public, message, signature)

            // then
            assertFalse(result)
        }
    }

    @Nested
    @DisplayName("서명화 데이터 테스트")
    inner class SignMessageTest {

        @Test
        fun `서명화 메시지 검증`() {
            val message = TestMessageFactory.create(secretKey)

            // 1단계: 서명 검증
            val signature = signer.sign(keyPair.private, message)
            val sigValid = signer.verify(keyPair.public, message, signature)
            assertTrue(sigValid)

            // 2단계: JSON 기반 hash 검증
            val hashValid = JsonHashVerifier.verify(message, secretKey)
            assertTrue(hashValid)
        }
    }
}
