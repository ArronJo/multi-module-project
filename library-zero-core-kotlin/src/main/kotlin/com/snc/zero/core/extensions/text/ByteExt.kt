package com.snc.zero.core.extensions.text

fun String.toBytes(): ByteArray {
    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((this[i].digitToInt(16) shl 4) + (this[i + 1].digitToInt(16))).toByte()
        i += 2
    }
    return data
}
