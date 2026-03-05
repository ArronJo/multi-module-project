package com.snc.zero.crypto.password.generator

import java.security.SecureRandom
import java.util.Base64

/**
 * 랜덤 키 생성
 * → npx @better-auth/cli@latest secret 과 동일한 방식
 *   : https://github.com/better-auth/better-auth
 * “secret”은 문서 기준으로
 * openssl rand -base64 32와 동급(= 32바이트 고엔트로피 랜덤 → Base64 인코딩)
 */
object BetterAuthGenerator {

    /**
     * 암호학적으로 안전한 랜덤 시크릿 키 생성
     * @param byteLength 키 길이 (기본 32바이트 = 256bit)
     */
    fun generate(byteLength: Int = 32): ByteArray {
        return ByteArray(byteLength).also { SecureRandom().nextBytes(it) }
    }

    fun generateAsBase64(byteLength: Int = 32): String {
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(generate(byteLength))
    }
}
