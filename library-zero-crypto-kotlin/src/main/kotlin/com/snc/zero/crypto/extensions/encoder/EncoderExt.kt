package com.snc.zero.crypto.extensions.encoder

import com.snc.zero.crypto.encoder.Encoder

fun String.encodeBase64(): String {
    return this.toByteArray().encodeBase64()
}

fun ByteArray.encodeBase64(): String {
    return Encoder.with(Encoder.Algo.BASE64).encode(this)
}
