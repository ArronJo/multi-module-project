package com.snc.zero.crypto.hash.sha3

import com.snc.zero.crypto.hash.base.BaseHash
import org.bouncycastle.crypto.digests.SHA3Digest
import java.nio.charset.Charset

object SHA3: BaseHash() {

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
        val digest = SHA3Digest(bitLength)
        val ib = msg.toByteArray(charSet)
        val ob = ByteArray(digest.digestSize)
        salt(digest, salt, charSet)
        digest.update(ib, 0, ib.size)
        iteration(digest, ib, iterationCount)
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