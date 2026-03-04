package com.snc.zero.crypto.cipher.ecc

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

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
        require(length > 0) { "length must be > 0" }
        require(length <= 255 * HASH_LEN) { "length must be <= ${255 * HASH_LEN}" }

        val realSalt = salt ?: ByteArray(HASH_LEN) { 0 }

        // Step 1: Extract
        val prk = hmac(realSalt, ikm)

        // Step 2: Expand
        val n = (length + HASH_LEN - 1) / HASH_LEN

        val macTemplate = Mac.getInstance(HMAC)
        macTemplate.init(SecretKeySpec(prk, HMAC))

        var t = ByteArray(0)
        val okm = ByteArray(length)
        var pos = 0

        try {
            for (i in 1..n) {
                val mac = macTemplate.clone() as Mac
                mac.update(t)
                if (info != null) mac.update(info)
                mac.update(i.toByte())
                t = mac.doFinal()

                val copyLen = minOf(t.size, length - pos)
                System.arraycopy(t, 0, okm, pos, copyLen)
                pos += copyLen
            }
        } finally {
            prk.fill(0)
            t.fill(0)
        }

        return okm
    }

    private fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance(HMAC)
        mac.init(SecretKeySpec(key, HMAC))
        return mac.doFinal(data)
    }
}
