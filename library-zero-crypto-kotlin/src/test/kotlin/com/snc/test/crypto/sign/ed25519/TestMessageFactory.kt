package com.snc.test.crypto.sign.ed25519

import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TestMessageFactory {

    private const val HMAC_ALGO = "HmacSHA256"

    // 테스트용 고정 시크릿 (실서비스에서는 절대 하드코딩 금지)
    private val secretKey = "test-hmac-secret-key-2026"
        .toByteArray(Charsets.UTF_8)

    fun create(): ByteArray {
        val dateTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val baseMessage = """
            일시:$dateTime
            장소:서울 강남구
            사용자:홍길동
            메뉴:아메리카노 Tall
            목적:업무 미팅
            금액:4500원
        """.trimIndent()

        val hmac = hmacSha256(baseMessage)

        val finalMessage = """
            $baseMessage
            HASH:$hmac
        """.trimIndent()

        return finalMessage.toByteArray(Charsets.UTF_8)
    }

    private fun hmacSha256(data: String): String {
        val mac = Mac.getInstance(HMAC_ALGO)
        val keySpec = SecretKeySpec(secretKey, HMAC_ALGO)
        mac.init(keySpec)
        val hashBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))

        return hashBytes.joinToString("") {
            "%02x".format(it)
        }
    }

    // SHA-256 해시 함수
    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))

        return hashBytes.joinToString("") {
            "%02x".format(it)
        }
    }
}
