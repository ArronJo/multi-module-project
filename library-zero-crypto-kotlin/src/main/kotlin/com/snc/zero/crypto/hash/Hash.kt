package com.snc.zero.crypto.hash

import com.snc.zero.crypto.hash.sha2.SHA2
import com.snc.zero.crypto.hash.sha3.SHA3
import com.snc.zero.crypto.hash.sha3.SHAKE
import java.nio.charset.Charset
import kotlin.jvm.Throws

/**
 * Hash 알고리즘
 * (메소드 체이닝 (Method Chaining) 활용)
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class Hash private constructor(var algo: Algo) {
    private val msgEmptyKey = "Empty key"

    companion object {
        @JvmStatic
        fun with(algo: Algo = Algo.SHA256): Hash {
            return Hash(algo)
        }
    }

    enum class Algo {
        HmacSHA224,
        HmacSHA256,
        HmacSHA384,
        HmacSHA512,
        SHA224,
        SHA256,
        SHA384,
        SHA512,
        SHA3_224,
        SHA3_256,
        SHA3_384,
        SHA3_512,
        SHAKE128,
        SHAKE256
    }

    private var key: String = ""
    private var salt: String = ""
    private var iterationCount: Int = 0
    private var charSet: Charset = Charsets.UTF_8

    fun key(key: String): Hash {
        this.key = key
        return this
    }

    fun salt(salt: String): Hash {
        this.salt = salt
        return this
    }

    fun iterationCount(count: Int): Hash {
        this.iterationCount = count
        return this
    }

    fun charSet(charSet: Charset): Hash {
        this.charSet = charSet
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun digest(msg: String): ByteArray {
        return when (this.algo) {
            Algo.HmacSHA224 -> {
                require(key.isNotEmpty()) { msgEmptyKey }
                return SHA2.hmacSHA224(msg, key, salt, iterationCount, charSet)
            }
            Algo.HmacSHA256 -> {
                require(key.isNotEmpty()) { msgEmptyKey }
                return SHA2.hmacSHA256(msg, key, salt, iterationCount, charSet)
            }
            Algo.HmacSHA384 -> {
                require(key.isNotEmpty()) { msgEmptyKey }
                return SHA2.hmacSHA384(msg, key, salt, iterationCount, charSet)
            }
            Algo.HmacSHA512 -> {
                require(key.isNotEmpty()) { msgEmptyKey }
                return SHA2.hmacSHA512(msg, key, salt, iterationCount, charSet)
            }
            Algo.SHA224 -> {
                SHA2.sha224(msg, salt, iterationCount, charSet)
            }
            Algo.SHA256 -> {
                SHA2.sha256(msg, salt, iterationCount, charSet)
            }
            Algo.SHA384 -> {
                SHA2.sha384(msg, salt, iterationCount, charSet)
            }
            Algo.SHA512 -> {
                SHA2.sha512(msg, salt, iterationCount, charSet)
            }
            Algo.SHA3_224 -> {
                SHA3.sha224(msg, salt, iterationCount, charSet)
            }
            Algo.SHA3_256 -> {
                SHA3.sha256(msg, salt, iterationCount, charSet)
            }
            Algo.SHA3_384 -> {
                SHA3.sha384(msg, salt, iterationCount, charSet)
            }
            Algo.SHA3_512 -> {
                SHA3.sha512(msg, salt, iterationCount, charSet)
            }
            Algo.SHAKE128 -> {
                SHAKE.shake128(msg, salt, iterationCount, charSet)
            }
            Algo.SHAKE256 -> {
                SHAKE.shake256(msg, salt, iterationCount, charSet)
            }
        }
    }
}
