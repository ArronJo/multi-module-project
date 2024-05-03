package com.snc.zero.crypto.cipher

import com.snc.zero.crypto.cipher.aes.AES
import com.snc.zero.crypto.encoder.Decoder
import com.snc.zero.crypto.encoder.Encoder

class Cipher private constructor(var algo: Algo = Algo.AES) {

    companion object {

        @JvmStatic
        fun with(algo: Algo = Algo.AES): Cipher {
            return Cipher(algo)
        }
    }

    enum class Algo {
        AES
    }

    private var key: String = ""
    private var iv: String = ""

    fun algo(algo: Algo): Cipher {
        this.algo = algo
        return this
    }

    fun key(key: String, iv: String): Cipher {
        this.key = key
        this.iv = iv
        return this
    }

    fun encrypt(input: ByteArray): String {
        return when (this.algo) {
            Algo.AES -> {
                val enc = AES.encrypt(this.key, this.iv, input)
                Encoder.with(Encoder.Algo.BASE64).encode(enc)
            }
        }
    }

    fun decrypt(input: String): ByteArray {
        return when (this.algo) {
            Algo.AES -> {
                val decoded = Decoder.with(Decoder.Algo.BASE64).decode(input)
                AES.decrypt(this.key, this.iv, decoded)
            }
        }
    }
}