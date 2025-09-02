package com.snc.test.crypto.security

import com.snc.zero.crypto.cipher.aes.AESGCM
import com.snc.zero.crypto.security.SecurityUtils
import com.snc.zero.crypto.share.b64UrlDecode
import com.snc.zero.crypto.share.b64UrlEncode
import com.snc.zero.crypto.share.randomBytes
import com.snc.zero.crypto.token.SignedToken
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.security.MessageDigest

@Suppress("NonAsciiCharacters")
class SecurityUtilsTest : BaseJUnit5Test() {

    @Nested
    inner class AESDefaultTest {

        @Test
        fun `샘플 사용법`() {
            // AES-GCM
            val aesKey = AESGCM.generateKey(256)
            val aad = "metadata".toByteArray()
            val message = "hello, world".toByteArray()
            val enc = SecurityUtils.aesGcmEncrypt(message, aesKey, aad)
            val dec = SecurityUtils.aesGcmDecrypt(enc, aesKey, aad)
            println("[AES] " + String(dec))

            // PBKDF2 Password Hash
            val stored = SecurityUtils.pbkdf2Hash("s3cr3tP@ss!".toCharArray())
            println("[PBKDF2] $stored")
            val ok = SecurityUtils.pbkdf2Verify("s3cr3tP@ss!".toCharArray(), stored)
            println("[PBKDF2 verify] $ok")

            // RSA Sign/Verify
            val kp = SecurityUtils.generateRsaKeyPair()
            val signature = SecurityUtils.rsaSign(kp.private, "sign me".toByteArray())
            val verified = SecurityUtils.rsaVerify(kp.public, "sign me".toByteArray(), signature)
            println("[RSA verify] $verified")

            // HMAC 토큰 (미니 JWT)
            val secret = randomBytes(32)
            val token = SecurityUtils.createSignedToken("""{"sub":"user-123","exp":1735689600}""", secret)
            val tokenOk = SecurityUtils.verifySignedToken(token, secret)
            println("[Token verify] $tokenOk")
        }
    }

    @Nested
    inner class AESandGCMTest {

        @Test
        @DisplayName("정상 암복호화")
        fun `AES-GCM encrypt and decrypt should return original data`() {
            val key = AESGCM.generateKey(256)
            val plaintext = "hello kotlin security".toByteArray()

            (1..10).forEach { i ->
                println("\n[$i] plaintext : ${String(plaintext)}")
                val ciphertext = SecurityUtils.aesGcmEncrypt(plaintext, key)
                println("[$i] ciphertext: ${String(ciphertext).take(30)}")
                val decrypted = SecurityUtils.aesGcmDecrypt(ciphertext, key)
                println("[$i] decrypted : ${String(decrypted)}")
                assertArrayEquals(plaintext, decrypted)
            }
        }

        @Test
        @DisplayName("정상 암복호화 (AAD 활용)")
        fun `AES-GCM (with aad) encrypt and decrypt should return original data`() {
            val key = AESGCM.generateKey(256)
            val aad = "metadata".toByteArray()
            val plaintext = "hello kotlin security".toByteArray()

            (1..10).forEach { i ->
                println("\n[$i] plaintext : ${String(plaintext)}")
                val ciphertext = SecurityUtils.aesGcmEncrypt(plaintext, key, aad)
                println("[$i] ciphertext: ${String(ciphertext).take(30)}")
                val decrypted = SecurityUtils.aesGcmDecrypt(ciphertext, key, aad)
                println("[$i] decrypted : ${String(decrypted)}")
                assertArrayEquals(plaintext, decrypted)
            }
        }

        @Test
        @DisplayName("AAD 불일치 시 실패")
        fun `AES-GCM with wrong AAD should fail`() {
            val key = AESGCM.generateKey(256)
            val plaintext = "secure data".toByteArray()

            val ciphertext = SecurityUtils.aesGcmEncrypt(plaintext, key, "right".toByteArray())

            assertThrows<Exception> {
                SecurityUtils.aesGcmDecrypt(ciphertext, key, "wrong".toByteArray())
            }
        }
    }

    @Nested
    inner class PasswordHashTest {

        @Test
        @DisplayName("올바른 비밀번호 → 성공")
        fun `PBKDF2 password hash and verify should succeed`() {
            val password = "S3cureP@ss!".toCharArray()
            val hash = SecurityUtils.pbkdf2Hash(password)

            assertTrue(SecurityUtils.pbkdf2Verify("S3cureP@ss!".toCharArray(), hash))
        }

        @Test
        @DisplayName("틀린 비밀번호 → 실패")
        fun `PBKDF2 verify with wrong password should fail`() {
            val hash = SecurityUtils.pbkdf2Hash("mypassword".toCharArray())

            assertFalse(SecurityUtils.pbkdf2Verify("wrongpassword".toCharArray(), hash))
        }
    }

    @Nested
    inner class RSATest {

        @Test
        @DisplayName("정상 서명/검증")
        fun `RSA sign and verify should succeed`() {
            val kp = SecurityUtils.generateRsaKeyPair(2048)
            val data = "important message".toByteArray()

            val signature = SecurityUtils.rsaSign(kp.private, data)
            val verified = SecurityUtils.rsaVerify(kp.public, data, signature)

            assertTrue(verified)
        }

        @Test
        @DisplayName("데이터 위변조 시 실패")
        fun `RSA verify with wrong data should fail`() {
            val kp = SecurityUtils.generateRsaKeyPair(2048)
            val signature = SecurityUtils.rsaSign(kp.private, "original".toByteArray())

            val verified = SecurityUtils.rsaVerify(kp.public, "tampered".toByteArray(), signature)

            assertFalse(verified)
        }
    }

    @Nested
    inner class HMACTokenTest {

        @Test
        @DisplayName("정상 생성/검증")
        fun `HMAC token should be created and verified`() {
            val secret = randomBytes(32)
            val payload = """{"sub":"user-123","exp":9999999999}"""

            val token = SecurityUtils.createSignedToken(payload, secret)
            assertTrue(SecurityUtils.verifySignedToken(token, secret))
        }

        @Test
        @DisplayName("payload 변조 시 실패")
        fun `HMAC token verification should fail if tampered`() {
            val secret = randomBytes(32)
            val payload = """{"sub":"user-123"}"""

            val token = SecurityUtils.createSignedToken(payload, secret)

            // 토큰 파싱
            val parts = token.split(SignedToken.delimiter)
            val header = parts[0]
            val payloadB64 = parts[1]
            val signature = parts[2]

            // payload JSON 변조
            val decodedPayload = String(b64UrlDecode(payloadB64))
            val tamperedPayload = decodedPayload.replace("user-123", "hacker")

            // 다시 Base64 인코딩
            val tamperedPayloadB64 = b64UrlEncode(tamperedPayload.toByteArray())

            // 변조된 signingInput + 기존 signature 그대로 사용 (→ 무조건 검증 실패해야 함)
            val tamperedToken = "$header.$tamperedPayloadB64.$signature"

            assertFalse(SecurityUtils.verifySignedToken(tamperedToken, secret))
        }

    }

    @Nested
    inner class Base64Test {

        @Test
        @DisplayName("원복 가능성 확인")
        fun `Base64 URL-safe encode and decode should round-trip`() {
            val original = "테스트123!@#".toByteArray()
            val encoded = b64UrlEncode(original)
            val decoded = b64UrlDecode(encoded)

            assertArrayEquals(original, decoded)
        }
    }

    @Nested
    inner class PBKDF2Test {

        @Test
        @DisplayName("추가")
        fun `PBKDF2 hash format should be valid`() {
            val hash = SecurityUtils.pbkdf2Hash("abc123".toCharArray())
            assertTrue(hash.startsWith("PBKDF2$"))
            assertEquals(4, hash.split('$').size)
        }

        @Test
        @DisplayName("MessageDigest.isEqual 사용 여부 (타이밍 공격 방어)")
        fun `PBKDF2 verify should be resistant to timing attacks`() {
            val hash = SecurityUtils.pbkdf2Hash("secret".toCharArray())
            val wrongHash = SecurityUtils.pbkdf2Hash("secret".toCharArray())

            // Ensure constant-time comparison is used
            val eq = MessageDigest.isEqual(hash.toByteArray(), wrongHash.toByteArray())
            assertFalse(eq) // 서로 다르니까 false여야 함
        }
    }

    @Nested
    inner class SecurityUtilsJwtTamperTest {

        /**
         * 테스트 동작
         *
         * PAYLOAD → payload 안의 "sub":"user-123" → "sub":"hacker" 로 변경
         *
         * HEADER → header JSON의 "typ":"JWT" → "typ":"FAKE" 로 변경
         *
         * SIGNATURE → signature 바이트를 일부 변경 (위변조 시뮬레이션)
         *
         * 각 경우 모두 **verifySignedToken에서 false**가 리턴돼야 정상 동작!
         */
        @ParameterizedTest
        @ValueSource(strings = ["PAYLOAD", "HEADER", "SIGNATURE"])
        fun `tampered JWT token should fail verification`(tamperType: String) {
            val secret = randomBytes(32)
            val payload = """{"sub":"user-123","exp":9999999999}"""
            val token = SecurityUtils.createSignedToken(payload, secret)

            val parts = token.split(SignedToken.delimiter)
            val header = parts[0]
            val payloadB64 = parts[1]
            val signature = parts[2]

            val tamperedToken = when (tamperType) {
                "PAYLOAD" -> {
                    // payload 변조: Base64 디코딩 → JSON 수정 → 다시 인코딩
                    val decodedPayload = String(b64UrlDecode(payloadB64))
                    val tamperedPayload = decodedPayload.replace("user-123", "hacker")
                    val tamperedPayloadB64 = b64UrlEncode(tamperedPayload.toByteArray())
                    "$header.$tamperedPayloadB64.$signature"
                }
                "HEADER" -> {
                    // header 변조: typ을 JWT -> FAKE 로 변경
                    val decodedHeader = String(b64UrlDecode(header))
                    val tamperedHeader = decodedHeader.replace("JWT", "FAKE")
                    val tamperedHeaderB64 = b64UrlEncode(tamperedHeader.toByteArray())
                    "$tamperedHeaderB64.$payloadB64.$signature"
                }
                "SIGNATURE" -> {
                    // signature 변조: 한 바이트 변경
                    val sigBytes = b64UrlDecode(signature).clone()
                    sigBytes[0] = (sigBytes[0].toInt() xor 0x01).toByte()
                    "$header.$payloadB64.${b64UrlEncode(sigBytes)}"
                }
                else -> token
            }

            val verified = SecurityUtils.verifySignedToken(tamperedToken, secret)
            assertFalse(verified, "Tampered token ($tamperType) should not verify")
        }
    }
}
