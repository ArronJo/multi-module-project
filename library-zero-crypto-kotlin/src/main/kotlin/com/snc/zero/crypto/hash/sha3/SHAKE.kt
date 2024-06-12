package com.snc.zero.crypto.hash.sha3

import com.snc.zero.crypto.hash.base.BaseHash
import org.bouncycastle.crypto.digests.SHAKEDigest
import java.nio.charset.Charset

object SHAKE: BaseHash() {

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
        iteration(digest, ib, iterationCount)
        digest.doOutput(ob, 0, ob.size)
        return ob
    }

    private fun checkBitLength(bitLength: Int): Int {
        when (bitLength) {
            128, 256 -> return bitLength
            else -> throw IllegalArgumentException("'bitLength' $bitLength not supported")
        }
    }
}