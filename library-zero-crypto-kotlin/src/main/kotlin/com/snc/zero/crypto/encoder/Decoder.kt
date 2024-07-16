package com.snc.zero.crypto.encoder

import com.snc.zero.crypto.encoder.base64.Base64

class Decoder private constructor(private var algo: Algo) {

    companion object {

        fun with(algo: Algo = Algo.BASE64): Decoder {
            return Decoder(algo)
        }
    }

    enum class Algo {
        BASE64
    }

    fun decode(data: String): ByteArray {
        return when (this.algo) {
            Algo.BASE64 -> {
                Base64.decode(data)
            }
        }
    }
}