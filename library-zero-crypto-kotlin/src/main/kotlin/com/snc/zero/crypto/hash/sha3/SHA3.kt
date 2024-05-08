package com.snc.zero.crypto.hash.sha3

import org.bouncycastle.crypto.digests.SHA3Digest
import java.nio.charset.Charset

object SHA3 {

    fun sha224(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 224, salt, iterationCount, charSet)
    }

    fun sha256(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 256, salt, iterationCount, charSet)
    }

    fun sha384(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 384, salt, iterationCount, charSet)
    }

    fun sha512(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 512, salt, iterationCount, charSet)
    }

    private fun digest(msg: String, bitLength: Int = 256, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        checkBitLength(bitLength)
        val digest = SHA3Digest(if (bitLength == 128) { 256 } else { bitLength })
        val ib = msg.toByteArray(charSet)
        val ob = ByteArray(digest.digestSize)
        if (salt.isNotEmpty()) {
            val sb = salt.toByteArray(charSet)
            digest.update(sb, 0, sb.size)
        }
        digest.update(ib, 0, ib.size)
        for (i in 1..iterationCount) {
            digest.update(ib, 0, ib.size)
        }
        digest.doFinal(ob, 0)
        return ob
    }

    private fun checkBitLength(bitLength: Int): Int {
        when (bitLength) {
            224, 256, 384, 512 -> return bitLength
            else -> throw IllegalArgumentException("'bitLength' $bitLength not supported")
        }
    }
}