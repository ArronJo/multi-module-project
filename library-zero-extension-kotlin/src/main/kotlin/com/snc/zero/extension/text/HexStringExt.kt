package com.snc.zero.extension.text

/*
    원시타입 vs 참조타입
    : https://velog.io/@gillog/%EC%9B%90%EC%8B%9C%ED%83%80%EC%9E%85-%EC%B0%B8%EC%A1%B0%ED%83%80%EC%9E%85Primitive-Type-Reference-Type

    // 제일 무거움
    fun ByteArray.toHexString(): String {
        return this.fold("") { str, it -> str + "%02x".format(it) }
    }

    // 중간 무거움
    fun ByteArray.toHexString(): String {
        val sb = java.lang.StringBuilder()
        for (i in this.indices) {
            sb.append((this[i].toInt() and 0xff)
                .toString(16)).substring(1)
        }
        return sb.toString()
    }

    fun ByteArray.toHexString(): String {
        val hexChars = CharArray(this.size * 2)
        for (i in this.indices) {
            val v = this[i].toInt() and 0xFF
            hexChars[i * 2] = hexDigits[v ushr 4]
            hexChars[i * 2 + 1] = hexDigits[v and 0x0F]
        }
        return String(hexChars)
    }
 */

private val hexDigits = "0123456789abcdef".toCharArray()

// 제일 가벼움
// Kotlin: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-hex-string.html
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

fun Char.toHexString(): String =
    if (code in 0..127) "%02x".format(code) else "%04x".format(code)

fun String.toHexString(): String {
    return this.toByteArray().toHexString()
}
