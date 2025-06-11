package com.snc.zero.crypto.hash.sha3

import com.snc.zero.crypto.hash.base.BaseHash
import org.bouncycastle.crypto.digests.SHA3Digest
import java.nio.charset.Charset

/**
 * SHA-3 (Keccak 알고리즘) Group
 *
 * https://en.wikipedia.org/wiki/SHA-3
 * https://www.bouncycastle.org/latest_releases.html
 * https://downloads.bouncycastle.org/betas/
 * https://github.com/bcgit/bc-java/blob/main/core/src/main/java/org/bouncycastle/crypto/engines/AESEngine.java
 * https://github.com/bcgit/bc-kotlin
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object SHA3 : BaseHash() {

    fun sha128(msg: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, 128, salt, iterationCount, charSet)
    }

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

    fun digest(msg: String, bitLength: Int, salt: String, iterationCount: Int, charSet: Charset): ByteArray {
        checkBitLength(bitLength)
        val digest = SHA3Digest(if (bitLength == 128) {
            256
        } else {
            bitLength
        })
        val ib = msg.toByteArray(charSet)
        val ob = ByteArray(digest.digestSize)
        salt(digest, salt, charSet)
        digest.update(ib, 0, ib.size)
        iteration(digest, ib, iterationCount)
        digest.doFinal(ob, 0)
        if (bitLength == 128) {
            return ob.copyOf(16)
        }
        return ob
    }

    private fun checkBitLength(bitLength: Int): Int {
        when (bitLength) {
            128, 224, 256, 384, 512 -> return bitLength
            else -> throw IllegalArgumentException("'bitLength' $bitLength not supported")
        }
    }
}
