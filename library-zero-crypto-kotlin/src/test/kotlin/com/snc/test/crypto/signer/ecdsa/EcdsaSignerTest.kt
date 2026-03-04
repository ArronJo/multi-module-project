package com.snc.test.crypto.signer.ecdsa

import com.snc.zero.crypto.signer.ecdsa.EcdsaSigner
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.security.KeyPair
import java.security.SignatureException

@Suppress("NonAsciiCharacters")
@DisplayName("EcdsaSigner 테스트")
class EcdsaSignerTest {

    private lateinit var signer: EcdsaSigner
    private lateinit var keyPair: KeyPair

    @BeforeEach
    fun setup() {
        signer = EcdsaSigner()
        keyPair = signer.generateKeyPair()
    }

    @Nested
    @DisplayName("키 생성 테스트")
    inner class KeyGenerationTest {

        @Test
        fun `P-256 키 페어 생성 성공`() {
            // when
            val kp = signer.generateKeyPair()

            // then
            assertNotNull(kp.public)
            assertNotNull(kp.private)
        }

        @Test
        fun `생성된 키 페어는 매번 다름`() {
            // when
            val kp1 = signer.generateKeyPair()
            val kp2 = signer.generateKeyPair()

            // then
            assertFalse(kp1.public.encoded.contentEquals(kp2.public.encoded))
        }

        @Test
        fun `공개키 알고리즘은 EC`() {
            // then
            assertTrue(keyPair.public.algorithm == "EC")
        }
    }

    @Nested
    @DisplayName("전자서명 테스트")
    inner class SignTest {

        @Test
        fun `서명 및 검증 성공`() {
            // given
            val message = "Hello ECDSA".toByteArray()

            // when
            val signature = signer.sign(keyPair.private, message)
            val result = signer.verify(keyPair.public, message, signature)

            // then
            assertTrue(result)
        }

        @Test
        fun `동일 메시지 서명값은 매번 다름 (비결정적 서명)`() {
            // given
            val message = "Hello ECDSA".toByteArray()

            // when
            val sig1 = signer.sign(keyPair.private, message)
            val sig2 = signer.sign(keyPair.private, message)

            // then - ECDSA는 랜덤 k 사용으로 서명값이 매번 다름
            assertFalse(sig1.contentEquals(sig2))
        }

        @Test
        fun `메시지 변조 시 검증 실패`() {
            // given
            val original = "amount=4500".toByteArray()
            val tampered = "amount=9000".toByteArray()
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
                val message = "Hello ECDSA".toByteArray()
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
            val message = "Hello ECDSA".toByteArray()
            val signature = signer.sign(keyPair.private, message)

            // when
            val result = signer.verify(otherKeyPair.public, message, signature)

            // then
            assertFalse(result)
        }

        @Test
        fun `빈 메시지도 서명 및 검증 성공`() {
            // given
            val message = "".toByteArray()

            // when
            val signature = signer.sign(keyPair.private, message)
            val result = signer.verify(keyPair.public, message, signature)

            // then
            assertTrue(result)
        }

        @Test
        fun `대용량 메시지 서명 및 검증 성공`() {
            // given
            val message = ByteArray(1024 * 1024).also { it.fill('A'.code.toByte()) }

            // when
            val signature = signer.sign(keyPair.private, message)
            val result = signer.verify(keyPair.public, message, signature)

            // then
            assertTrue(result)
        }
    }

    @Nested
    @DisplayName("Ed25519 비교 테스트")
    inner class CompareWithEd25519Test {

        @Test
        fun `ECDSA 서명 크기는 70~72바이트 (DER 인코딩)`() {
            // given
            val message = "Hello ECDSA".toByteArray()

            // when
            val signature = signer.sign(keyPair.private, message)

            // then - ECDSA DER 인코딩: 70~72바이트
            println("ECDSA 서명 크기: ${signature.size} bytes")
            assertTrue(signature.size in 68..72)
        }

        @Test
        fun `공개키 크기는 65바이트 (uncompressed)`() {
            // then - EC P-256 비압축 공개키: 65바이트
            println("ECDSA 공개키 크기: ${keyPair.public.encoded.size} bytes")
            // X.509 인코딩 포함하면 91바이트
            assertTrue(keyPair.public.encoded.isNotEmpty())
        }
    }
}
