package com.snc.zero.crypto.extensions.decoder

import com.snc.zero.crypto.encoder.Decoder

fun String.decodeBase62(): ByteArray {
    return Decoder.with(Decoder.Algo.BASE62).decode(this)
}

fun String.decodeBase64(): ByteArray {
    return Decoder.with(Decoder.Algo.BASE64).decode(this)
}

fun String.decodeURIComponent(): String {
    return Decoder.with(Decoder.Algo.URIComponent).decodeURI(this)
}

fun String.decodeURI(): String {
    return Decoder.with(Decoder.Algo.URI).decodeURI(this)
}
