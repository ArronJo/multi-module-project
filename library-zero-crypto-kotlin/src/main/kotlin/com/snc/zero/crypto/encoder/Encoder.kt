package com.snc.zero.crypto.encoder

import com.snc.zero.crypto.encoder.base64.Base64

class Encoder(private var algo: Algo) {

    companion object {

        @JvmStatic
        fun with(algo: Algo = Algo.BASE64): Encoder {
            return Encoder(algo)
        }
    }

    enum class Algo {
        BASE64
    }

    fun encode(input: ByteArray): String {
        return when (this.algo) {
            Algo.BASE64 -> {
                Base64.encode(input)
            }
        }
    }
}