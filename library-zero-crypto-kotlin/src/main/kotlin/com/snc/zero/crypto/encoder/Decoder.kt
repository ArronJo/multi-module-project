package com.snc.zero.crypto.encoder

import com.snc.zero.crypto.encoder.base62.Base62
import com.snc.zero.crypto.encoder.url.URLDecoder

/**
 * Decoder
 * (메소드 체이닝 (Method Chaining) 활용)
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class Decoder private constructor(private var algo: Algo) {

    companion object {

        @JvmStatic
        fun with(algo: Algo = Algo.BASE64): Decoder {
            return Decoder(algo)
        }
    }

    enum class Algo {
        BASE62,
        BASE64,
        URIComponent,
        URI
    }

    fun decode(data: String): ByteArray {
        return when (this.algo) {
            Algo.BASE62 -> {
                Base62.decode(data.toByteArray())
            }
            Algo.BASE64 -> {
                java.util.Base64.getDecoder().decode(data)
            }
            else -> {
                throw IllegalArgumentException("'Algorithm' ${this.algo} not supported")
            }
        }
    }

    fun decodeURI(uri: String): String {
        return when (this.algo) {
            Algo.URIComponent, Algo.URI -> {
                URLDecoder.decodeURIComponent(uri)
            }
            else -> {
                throw IllegalArgumentException("'Algorithm' ${this.algo} not supported")
            }
        }
    }
}
