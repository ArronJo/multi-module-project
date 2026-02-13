package com.snc.zero.crypto.cipher

import com.snc.zero.crypto.cipher.aes.SimpleAES
import com.snc.zero.crypto.cipher.rsa.RSACipher
import com.snc.zero.crypto.encoder.Decoder
import com.snc.zero.crypto.encoder.Encoder
import java.security.Key

/**
 * 암호화 알고리즘
 * (메소드 체이닝 (Method Chaining) 활용)
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class Cipher private constructor(var algo: Algo = Algo.AES) {

    companion object {

        fun with(algo: Algo = Algo.AES): Cipher {
            return Cipher(algo)
        }
    }

    enum class Algo {
        AES,
        RSA
    }

    private var key: String = ""
    private var iv: String = ""
    private var rsaKey: Key? = null
    private var transform: String = ""

    fun algo(algo: Algo): Cipher {
        this.algo = algo
        return this
    }

    // for AES
    fun key(key: String, iv: String): Cipher {
        this.key = key
        this.iv = iv
        return this
    }

    // for RSA
    fun key(rsaKey: Key): Cipher {
        this.rsaKey = rsaKey
        return this
    }

    // for AES, RSA
    fun transform(transform: String): Cipher {
        this.transform = transform
        return this
    }

    fun encrypt(input: ByteArray): String {
        return when (algo) {
            Algo.AES -> {
                val enc = if (transform.isNotEmpty()) {
                    SimpleAES.encrypt(input, key, iv, transform)
                } else {
                    SimpleAES.encrypt(input, key, iv)
                }
                Encoder.with(Encoder.Algo.BASE64).encode(enc)
            }
            Algo.RSA -> {
                requireNotNull(rsaKey) { "Empty key" }
                val enc = RSACipher.encrypt(input, rsaKey!!, transform)
                Encoder.with(Encoder.Algo.BASE64).encode(enc)
            }
        }
    }

    fun decrypt(input: String): ByteArray {
        return when (algo) {
            Algo.AES -> {
                val decoded = Decoder.with(Decoder.Algo.BASE64).decode(input)
                if (transform.isNotEmpty()) {
                    SimpleAES.decrypt(decoded, key, iv, transform)
                } else {
                    SimpleAES.decrypt(decoded, key, iv)
                }
            }
            Algo.RSA -> {
                requireNotNull(rsaKey) { "Empty key" }
                val decoded = Decoder.with(Decoder.Algo.BASE64).decode(input)
                RSACipher.decrypt(decoded, rsaKey!!, transform)
            }
        }
    }
}
