package com.snc.zero.crypto.extensions.decoder

import com.snc.zero.crypto.encoder.Decoder

fun String.decodeBase64(): ByteArray {
    return Decoder.with(Decoder.Algo.BASE64).decode(this)
}
