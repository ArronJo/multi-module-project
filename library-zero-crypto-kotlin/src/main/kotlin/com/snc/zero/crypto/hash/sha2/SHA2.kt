package com.snc.zero.crypto.hash.sha2

import com.snc.zero.crypto.hash.base.BaseHash
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * SHA-2 Group
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object SHA2 : BaseHash() {

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
        (1..iterationCount).forEach { _ ->
            md.reset()
            hashed = md.doFinal(hashed)
        }
        return hashed
    }

    /**
     * SHA-2 Group 에는 128 이 없다. 꼼수로 만들어본다.
     * 128 크기로 만들고 싶다면 SHAKE128 을 사용하는 것이 더 좋다.
     */
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
        val alg = if (bitLength == 128) {
            "SHA-256"
        } else {
            "SHA-$bitLength"
        }
        val md = MessageDigest.getInstance(alg)
        md.reset()
        if (salt.isNotEmpty()) {
            md.update(salt.toByteArray(charSet))
        }
        var hashed = md.digest(msg.toByteArray(charSet))
        (1..iterationCount).forEach { _ ->
            md.reset()
            hashed = md.digest(hashed)
        }
        if (bitLength == 128) {
            return hashed.copyOf(16)
        }
        return hashed
    }

    private fun checkBitLength(bitLength: Int): Int {
        when (bitLength) {
            128, 224, 256, 384, 512 -> return bitLength
            else -> throw IllegalArgumentException("'bitLength' $bitLength not supported")
        }
    }
}
