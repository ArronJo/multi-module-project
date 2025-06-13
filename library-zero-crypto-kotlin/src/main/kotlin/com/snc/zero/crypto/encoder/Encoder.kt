package com.snc.zero.crypto.encoder

import com.snc.zero.crypto.encoder.base62.Base62
import com.snc.zero.crypto.encoder.url.URLEncoder

/**
 * Encoder
 * (메소드 체이닝 (Method Chaining) 활용)
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class Encoder private constructor(private var algo: Algo) {

    companion object {
        fun with(algo: Algo): Encoder {
            return Encoder(algo)
        }
    }

    enum class Algo {
        BASE62,
        BASE64,
        URIComponent,
        URI
    }

    fun encode(input: ByteArray): String {
        return when (this.algo) {
            Algo.BASE62 -> {
                String(Base62.encode(input))
            }
            Algo.BASE64 -> {
                java.util.Base64.getEncoder().encodeToString(input)
            }
            else -> {
                throw IllegalArgumentException("'Algorithm' ${this.algo} not supported")
            }
        }
    }

    fun encodeURI(uri: String, charset: String = "utf-8"): String {
        return when (this.algo) {
            Algo.URIComponent -> {
                URLEncoder.encodeURIComponent(uri, charset)
            }
            Algo.URI -> {
                URLEncoder.encodeURI(uri, charset)
            }
            else -> {
                throw IllegalArgumentException("'Algorithm' ${this.algo} not supported")
            }
        }
    }
}
