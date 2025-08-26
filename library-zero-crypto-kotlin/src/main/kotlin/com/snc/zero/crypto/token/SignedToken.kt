package com.snc.zero.crypto.token

import com.snc.zero.crypto.share.b64UrlDecode
import com.snc.zero.crypto.share.b64UrlEncode
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * HMAC-SHA256 & 간단 토큰 샘플 (미니 JWT)
 */
object SignedToken {

    val delimiter = '|'

    private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }

    /**
     * 매우 단순한 JWT 형태의 샘플 (학습용)
     * header: {"alg":"HS256","typ":"JWT"}
     */
    fun createSignedToken(payloadJson: String, secretKey: ByteArray): String {
        val header = """{"alg":"HS256","typ":"JWT"}"""
        val headerB64 = b64UrlEncode(header.toByteArray(Charsets.UTF_8))
        val payloadB64 = b64UrlEncode(payloadJson.toByteArray(Charsets.UTF_8))
        val signingInput = "$headerB64.$payloadB64".toByteArray(Charsets.UTF_8)
        val sig = hmacSha256(secretKey, signingInput)
        return "$headerB64$delimiter$payloadB64$delimiter${b64UrlEncode(sig)}"
    }

    fun verifySignedToken(token: String, secretKey: ByteArray): Boolean {
        val parts = token.split(delimiter)
        if (parts.size != 3) return false
        val (h, p, s) = parts
        val signingInput = "$h.$p".toByteArray(Charsets.UTF_8)
        val expected = hmacSha256(secretKey, signingInput)
        val provided = try { b64UrlDecode(s) } catch (_: IllegalArgumentException) { return false }
        return MessageDigest.isEqual(expected, provided)
    }
}
