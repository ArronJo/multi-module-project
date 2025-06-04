package com.snc.zero.extension.text

private val hexDigits = "0123456789abcdef".toCharArray()

fun ByteArray.toHexString(): String {
    val sb = StringBuilder(this.size * 2)
    for (b in this) {
        val i = b.toInt()
        sb.append(hexDigits[i shr 4 and 0xf])
            .append(hexDigits[i and 0xf])
    }
    return sb.toString()
}

fun Byte.toHexString(): String {
    val i = this.toInt()
    return "${hexDigits[i shr 4 and 0xf]}${hexDigits[i and 0xf]}"
}

fun String.toHexString(): String {
    return this.toByteArray().toHexString()
}
