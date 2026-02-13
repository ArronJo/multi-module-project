package com.snc.zero.crypto.cipher.ecc

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.ceil
import kotlin.math.min

object HKDF {

    private const val HMAC = "HmacSHA256"
    private const val HASH_LEN = 32 // SHA-256 = 32 bytes

    /**
     * RFC 5869 HKDF
     *
     * RFC 5869 HKDF(HMAC-based Extract-and-Expand Key Derivation Function)는
     * 마스터 키와 같은 원시 비밀 값에서 암호학적으로 강력한 대칭 키를 유도하는 표준화된 알고리즘
     */
    fun deriveKey(
        ikm: ByteArray, // Input Key Material (ECDH secret)
        length: Int, // output length
        salt: ByteArray? = null,
        info: ByteArray? = null
    ): ByteArray {
        val realSalt = salt ?: ByteArray(HASH_LEN) { 0 }

        // Step 1: Extract
        val prk = hmac(realSalt, ikm)

        // Step 2: Expand
        val n = ceil(length.toDouble() / HASH_LEN).toInt()

        var t = ByteArray(0)
        val okm = ByteArray(length)

        var pos = 0

        for (i in 1..n) {
            val mac = Mac.getInstance(HMAC)

            mac.init(SecretKeySpec(prk, HMAC))

            mac.update(t)

            if (info != null) {
                mac.update(info)
            }

            mac.update(i.toByte())

            t = mac.doFinal()

            val copyLen = min(t.size, length - pos)
            System.arraycopy(t, 0, okm, pos, copyLen)

            pos += copyLen
        }

        return okm
    }

    private fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance(HMAC)
        mac.init(SecretKeySpec(key, HMAC))
        return mac.doFinal(data)
    }
}
