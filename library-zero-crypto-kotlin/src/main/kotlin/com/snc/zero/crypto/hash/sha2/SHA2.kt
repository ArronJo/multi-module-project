package com.snc.zero.crypto.hash.sha2

import com.snc.zero.crypto.hash.base.BaseHash
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SHA2: BaseHash() {

    fun hmacSHA224(msg: String, key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, key, 224, salt, iterationCount, charSet)
    }

    fun hmacSHA256(msg: String, key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, key, 256, salt, iterationCount, charSet)
    }

    fun hmacSHA384(msg: String, key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, key, 384, salt, iterationCount, charSet)
    }

    fun hmacSHA512(msg: String, key: String, salt: String = "", iterationCount: Int = 0, charSet: Charset = Charsets.UTF_8): ByteArray {
        return digest(msg, key, 512, salt, iterationCount, charSet)
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

    private fun digest(msg: String, key: String, bitLength: Int, salt: String, iterationCount: Int, charSet: Charset): ByteArray {
        checkBitLength(bitLength)
        val alg = "HmacSHA$bitLength"
        val signingKey = SecretKeySpec(key.toByteArray(), alg)
        val md = Mac.getInstance(alg)
        md.init(signingKey)
        if (salt.isNotEmpty()) {
            md.update(salt.toByteArray(charSet))
        }
        var hashed = md.doFinal(msg.toByteArray(charSet))
        for (i in 1..iterationCount) {
            md.reset()
            hashed = md.doFinal(hashed)
        }
        return hashed
    }

    fun digest(msg: String, bitLength: Int, salt: String, iterationCount: Int, charSet: Charset): ByteArray {
        checkBitLength(bitLength)
        val alg = "SHA-$bitLength"
        val md = MessageDigest.getInstance(alg)
        md.reset()
        if (salt.isNotEmpty()) {
            md.update(salt.toByteArray(charSet))
        }
        var hashed = md.digest(msg.toByteArray(charSet))
        for (i in 1..iterationCount) {
            md.reset()
            hashed = md.digest(hashed)
        }
        return hashed
    }

    private fun checkBitLength(bitLength: Int): Int {
        when (bitLength) {
            224, 256, 384, 512 -> return bitLength
            else -> throw IllegalArgumentException("'bitLength' $bitLength not supported")
        }
    }
}