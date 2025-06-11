package com.snc.zero.crypto.extensions.encoder

import com.snc.zero.crypto.encoder.Encoder

fun String.encodeBase62(): String {
    return this.toByteArray().encodeBase62()
}

fun ByteArray.encodeBase62(): String {
    return Encoder.with(Encoder.Algo.BASE62).encode(this)
}

fun String.encodeBase64(): String {
    return this.toByteArray().encodeBase64()
}

fun ByteArray.encodeBase64(): String {
    return Encoder.with(Encoder.Algo.BASE64).encode(this)
}

fun String.encodeURIComponent(charset: String = "utf-8"): String {
    return Encoder.with(Encoder.Algo.URIComponent).encodeURI(this, charset)
}

fun String.encodeURI(charset: String = "utf-8"): String {
    return Encoder.with(Encoder.Algo.URI).encodeURI(this, charset)
}

fun HashMap<String, *>.toUrlEncoding(charset: String = "UTF-8"): HashMap<String, Any> {
    val n = HashMap<String, Any>()
    for ((key, value) in this) {
        if (this[key] is String) {
            n[key] = (value as String).encodeURIComponent(charset)
        } else {
            n[key] = value as Any
        }
    }
    return n
}
