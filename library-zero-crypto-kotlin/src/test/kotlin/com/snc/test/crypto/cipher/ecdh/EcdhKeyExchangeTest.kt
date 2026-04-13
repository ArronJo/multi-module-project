package com.snc.test.crypto.cipher.ecdh

import com.snc.zero.crypto.ecdh.EcdhKeyExchange
import com.snc.zero.extensions.text.toHexString
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.MessageDigest
import java.util.Base64

/**
 * 테스트 방법
 * 1. ecsh-client.html 파일을 브라우저에서 오픈한다.
 * 2. 브라우저에서 "Step 1: 클라어언트 키 생성" 버튼을 누른다.
 * 3. `EcdhClientHtmlTest` 테스트를 실행하여 "서버 공개키" 를 만든다.
 * 4. 만들어진 "서버 공개키"를 브라우저에 입력하고 "Step 2: 공유 비밀키 도출" 버튼을 누른다.
 *
 */
@Suppress("NonAsciiCharacters")
@DisplayName("EccKeyExchange 테스트")
class EcdhKeyExchangeTest {

    private lateinit var exchange: EcdhKeyExchange

    @BeforeEach
    fun setup() {
        exchange = EcdhKeyExchange()
    }

    @Nested
    @DisplayName("ecdh-client.html 테스트")
    inner class EcdhClientHtmlTest {

        @Test
        fun `ecdh-client html - 클라이언트 공개키 - 공유 비밀키 도출`() {
            // given
            // "클라이언트 공개키 (서버로 전송할 값)" 을 세팅하면 됨.
            val bob =
                "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEPhjGcxIvyHE33Oyrs6TQyjh59QOa2XQHF6XddhN2UEw49avH4RQyKisZXvJGhF75YSQGRxp5JsyUTMhNsLPucw=="

            val sharedSecret = EcdhKeyExchange.handleKeyExchange(bob)
            println("\n공유 비밀키 도출: $sharedSecret")
            println("\n")
        }
    }

    @Nested
    @DisplayName("키 생성 테스트")
    inner class KeyGenerationTest {

        @Test
        fun `키 페어 생성 성공`() {
            // when
            val holder = exchange.generateKeyPair()

            // then
            assertNotNull(holder.publicKey)
            assertNotNull(holder.privateKey)
            assertNotNull(holder.publicKeyBytes)
        }

        @Test
        fun `생성된 공개키 바이트는 비어있지 않음`() {
            // when
            val holder = exchange.generateKeyPair()

            // then
            assertTrue(holder.publicKeyBytes.isNotEmpty())
        }

        @Test
        fun `키 페어는 매번 다르게 생성됨`() {
            // when
            val holder1 = exchange.generateKeyPair()
            val holder2 = exchange.generateKeyPair()

            // then
            assertFalse(holder1.publicKeyBytes.contentEquals(holder2.publicKeyBytes))
        }

        @Test
        fun `공개키 알고리즘은 EC`() {
            // when
            val holder = exchange.generateKeyPair()

            // then
            assertTrue(holder.publicKey.algorithm == "EC")
        }
    }

    @Nested
    @DisplayName("키 교환 테스트")
    inner class KeyAgreementTest {

        @Test
        fun `Alice와 Bob의 공유 비밀키는 동일`() {
            // given
            val alice = exchange.generateKeyPair()
            val bob = exchange.generateKeyPair()

            // when
            val aliceShared = exchange.deriveSharedSecret(alice.privateKey, bob.publicKeyBytes)
            val bobShared = exchange.deriveSharedSecret(bob.privateKey, alice.publicKeyBytes)

            // then
            assertArrayEquals(aliceShared, bobShared)
        }

        @Test
        fun `공유 비밀키는 비어있지 않음`() {
            // given
            val alice = exchange.generateKeyPair()
            val bob = exchange.generateKeyPair()

            // when
            val sharedSecret = exchange.deriveSharedSecret(alice.privateKey, bob.publicKeyBytes)

            // then
            assertTrue(sharedSecret.isNotEmpty())
            println("공유 비밀키 크기: ${sharedSecret.size} bytes")
        }

        @Test
        fun `공유 비밀키는 32바이트 (P-256)`() {
            // given
            val alice = exchange.generateKeyPair()
            val bob = exchange.generateKeyPair()

            // when
            val sharedSecret = exchange.deriveSharedSecret(alice.privateKey, bob.publicKeyBytes)

            // then - P-256 ECDH 공유 비밀키는 32바이트
            assertTrue(sharedSecret.size == 32)
        }

        @Test
        fun `서로 다른 키 쌍끼리는 공유 비밀키가 다름`() {
            // given
            val alice = exchange.generateKeyPair()
            val bob = exchange.generateKeyPair()
            val carol = exchange.generateKeyPair()

            // when
            val aliceBobShared = exchange.deriveSharedSecret(alice.privateKey, bob.publicKeyBytes)
            val aliceCarolShared = exchange.deriveSharedSecret(alice.privateKey, carol.publicKeyBytes)

            // then
            assertFalse(aliceBobShared.contentEquals(aliceCarolShared))
        }

        @Test
        fun `동일 키 쌍으로 여러 번 도출해도 공유 비밀키는 항상 동일 (결정적)`() {
            // given
            val alice = exchange.generateKeyPair()
            val bob = exchange.generateKeyPair()

            // when
            val shared1 = exchange.deriveSharedSecret(alice.privateKey, bob.publicKeyBytes)
            val shared2 = exchange.deriveSharedSecret(alice.privateKey, bob.publicKeyBytes)

            // then - ECDH는 결정적 → 같은 입력이면 항상 같은 출력
            assertArrayEquals(shared1, shared2)
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    inner class ExceptionTest {

        @Test
        fun `잘못된 공개키 바이트로 공유 비밀키 도출 시 예외 발생`() {
            // given
            val alice = exchange.generateKeyPair()
            val invalidPublicKeyBytes = ByteArray(32) { it.toByte() } // 잘못된 키

            // then
            assertThrows<Exception> {
                exchange.deriveSharedSecret(alice.privateKey, invalidPublicKeyBytes)
            }
        }

        @Test
        fun `빈 공개키 바이트로 공유 비밀키 도출 시 예외 발생`() {
            // given
            val alice = exchange.generateKeyPair()

            // then
            assertThrows<Exception> {
                exchange.deriveSharedSecret(alice.privateKey, ByteArray(0))
            }
        }
    }

    @Nested
    @DisplayName("실사용 시나리오 테스트")
    inner class ScenarioTest {

        @Test
        fun `공유 비밀키로 AES 키 도출 가능 (SHA-256 해시)`() {
            // given
            val alice = exchange.generateKeyPair()
            val bob = exchange.generateKeyPair()

            val sharedSecret = exchange.deriveSharedSecret(alice.privateKey, bob.publicKeyBytes)
            println("공유 비밀키: ${sharedSecret.toHexString()}")

            // when - 공유 비밀키를 SHA-256 해시 → AES-256 키로 활용
            val aesKey = MessageDigest.getInstance("SHA-256")
                .digest(sharedSecret)

            // then
            assertTrue(aesKey.size == 32) // AES-256 = 32바이트
            println("AES-256 키 크기: ${aesKey.size} bytes")
        }

        @Test
        fun `공개키 바이트 직렬화 후 복원해도 키 교환 성공`() {
            // given - 네트워크 전송 시뮬레이션 (공개키 바이트 직렬화/역직렬화)
            val alice = exchange.generateKeyPair()
            val bob = exchange.generateKeyPair()

            // 네트워크 전송 시뮬레이션: Base64 인코딩 → 디코딩
            val encoded = Base64.getEncoder().encodeToString(bob.publicKeyBytes)
            val decoded = Base64.getDecoder().decode(encoded)

            // when
            val aliceShared = exchange.deriveSharedSecret(alice.privateKey, decoded)
            val bobShared = exchange.deriveSharedSecret(bob.privateKey, alice.publicKeyBytes)

            // then
            assertArrayEquals(aliceShared, bobShared)
        }
    }
}
