package com.snc.zero.crypto.signer.ed25519

import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * HMAC-SHA256 메시지 인증코드 생성기
 *
 * - HMAC은 서명이 아닌 "메시지 무결성 + 인증"용
 * - 동일한 secretKey를 송수신 양측이 공유해야 함
 * - secretKey는 반드시 외부(환경변수 or 시스템 프로퍼티)에서 주입할 것
 *
 * 운영 서버 설정 방법:
 *   ./gradlew bootRun -PHMAC_SECRET_KEY=real-prod-secret
 *   java -DHMAC_SECRET_KEY=real-prod-secret -jar app.jar
 *   export HMAC_SECRET_KEY=real-prod-secret
 */
class HmacSigner(secretKey: ByteArray) {

    private val secretKey: ByteArray = secretKey.copyOf()

    companion object {
        private const val HMAC_ALGO = "HmacSHA256"
        private const val ENV_KEY = "HMAC_SECRET_KEY"

        /**
         * 환경변수 또는 시스템 프로퍼티에서 키를 읽어 인스턴스 생성
         * 키가 없으면 IllegalStateException 발생 (운영 환경 설정 누락 방지)
         */
        fun fromEnvironment(): HmacSigner {
            val hmacKey = System.getProperty(ENV_KEY)
                ?: System.getenv(ENV_KEY)

            // 존재 여부만 로깅 (키 값 자체는 절대 출력 금지)
            println("HMAC_SECRET_KEY 설정 여부: ${!hmacKey.isNullOrBlank()}")

            check(!hmacKey.isNullOrBlank()) {
                "HMAC_SECRET_KEY must be configured via system property or environment variable."
            }

            return HmacSigner(hmacKey.toByteArray(Charsets.UTF_8))
        }

        val DEFAULT_HMAC_SECRET: ByteArray by lazy {
            val hmacKey = System.getProperty("HMAC_SECRET_KEY")
                ?: System.getenv("HMAC_SECRET_KEY")

            println("DEFAULT_HMAC_SECRET >> hamcKey 값 가져오기: $hmacKey")

            if (hmacKey.isNullOrBlank()) {
                //println("⚠️ HMAC_SECRET_KEY not configured. Using fallback secret.")
                //"fallback-secret-change-me".toByteArray(Charsets.UTF_8)
                println("⚠️ HMAC_SECRET_KEY not configured. Generating temporary key.")
                val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
                java.util.Base64.getEncoder()
                    .encodeToString(bytes)
                    .toByteArray(Charsets.UTF_8)
            } else {
                hmacKey.toByteArray(Charsets.UTF_8)
            }
        }

        fun hmacSha256(data: String, secretKey: ByteArray = DEFAULT_HMAC_SECRET): String {
            return HmacSigner(secretKey).sign(data)
        }
    }

    fun sign(message: String): String {
        val mac = Mac.getInstance(HMAC_ALGO)
        val keySpec = SecretKeySpec(secretKey, HMAC_ALGO)
        mac.init(keySpec)

        val hashBytes = mac.doFinal(message.toByteArray(Charsets.UTF_8))

        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verify(message: String, expectedHmac: String): Boolean {
        return sign(message) == expectedHmac
    }
}
