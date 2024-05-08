package com.snc.zero.crypto.hash.sha3

import org.bouncycastle.crypto.digests.SHAKEDigest
import java.nio.charset.Charset

object SHAKE {

    fun shake128(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 128, salt, iterationCount, charSet)
    }

    fun shake256(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 256, salt, iterationCount, charSet)
    }

    private fun digest(msg: String, bitLength: Int = 128, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        checkBitLength(bitLength)
        val digest = SHAKEDigest(bitLength)
        val ib = msg.toByteArray(charSet)
        val ob = ByteArray(bitLength / 8)
        salt(digest, salt, charSet)
        digest.update(ib, 0, ib.size)
        iteration(digest, ib, iterationCount, charSet)
        digest.doOutput(ob, 0, ob.size)
        return ob
    }

    private fun salt(digest: org.bouncycastle.crypto.digests.KeccakDigest, salt: String = "", charSet: Charset = Charsets.UTF_8) {
        if (salt.isNotEmpty()) {
            val sb = salt.toByteArray(charSet)
            digest.update(sb, 0, sb.size)
        }
    }

    private fun iteration(digest: org.bouncycastle.crypto.digests.KeccakDigest, ib: ByteArray, iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8) {
        for (i in 1..iterationCount) {
            digest.update(ib, 0, ib.size)
        }
    }

    private fun checkBitLength(bitLength: Int): Int {
        when (bitLength) {
            128, 256 -> return bitLength
            else -> throw IllegalArgumentException("'bitLength' $bitLength not supported")
        }
    }
}