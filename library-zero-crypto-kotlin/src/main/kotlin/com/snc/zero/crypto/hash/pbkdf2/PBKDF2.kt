package com.snc.zero.crypto.hash.pbkdf2

import com.snc.zero.crypto.share.b64UrlDecode
import com.snc.zero.crypto.share.b64UrlEncode
import com.snc.zero.crypto.share.randomBytes
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Password Hash (PBKDF2-SHA256)
 */
object PBKDF2 {

    data class Pbkdf2Params(val iterations: Int = 210_000, val saltLen: Int = 16, val dkLen: Int = 32)

    /**
     * 저장 포맷: PBKDF2$<iterations>$<salt_b64url>$<dk_b64url>
     */
    fun pbkdf2Hash(password: CharArray, params: Pbkdf2Params = Pbkdf2Params()): String {
        val salt = randomBytes(params.saltLen)
        val dk = pbkdf2(password, salt, params.iterations, params.dkLen)
        // (선택) password 배열 정리: 호출측에서 재사용 안 하면 지우는 걸 권장
        java.util.Arrays.fill(password, '\u0000')
        return buildString {
            append("PBKDF2$")
            append(params.iterations)
            append('$')
            append(b64UrlEncode(salt))
            append('$')
            append(b64UrlEncode(dk))
        }
    }

    fun pbkdf2Verify(password: CharArray, stored: String): Boolean {
        val parts = stored.split('$')
        require(parts.size == 4 && parts[0] == "PBKDF2") { "Invalid PBKDF2 format" }
        val iterations = parts[1].toInt()
        val salt = b64UrlDecode(parts[2])
        val expected = b64UrlDecode(parts[3])
        val dk = pbkdf2(password, salt, iterations, expected.size)
        java.util.Arrays.fill(password, '\u0000')
        return MessageDigest.isEqual(dk, expected)
    }

    private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int, dkLen: Int): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, dkLen * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return skf.generateSecret(spec).encoded
    }
}
