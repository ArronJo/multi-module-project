package com.snc.zero.crypto.encoder.base64

object Base64 {

    private val TABLE = charArrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
        'w', 'x', 'y', 'z', '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', '-', '_', // '+', '/'
    )

    fun encode(data: ByteArray): String {
        val result = StringBuilder()
        var i = 0
        var a: Int
        var b: Int
        var c: Int

        while (i + 2 < data.size) {
            a = data[i].toInt()
            b = data[i + 1].toInt()
            c = data[i + 2].toInt()
            result.append(TABLE[a shr 2 and 0x3f])
            result.append(TABLE[(a shl 4 and 0x30) + (b shr 4 and 0x0f)])
            result.append(TABLE[(b shl 2 and 0x3c) + (c shr 6 and 0x03)])
            result.append(TABLE[c and 0x3f])
            i += 3
        }
        when (data.size - i) {
            1 -> {
                a = data[i].toInt()
                result.append(TABLE[a shr 2 and 0x3f])
                result.append(TABLE[a shl 4 and 0x30])
                result.append("==")
            }
            2 -> {
                a = data[i].toInt()
                b = data[i + 1].toInt()
                result.append(TABLE[a shr 2 and 0x3f])
                result.append(TABLE[(a shl 4 and 0x30) + (b shr 4 and 0x0f)])
                result.append(TABLE[b shl 2 and 0x3c])
                result.append("=")
            }
        }
        return result.toString()
    }

    fun decode(data: String): ByteArray {
        var padding = if (data[data.length - 1] == '=') {
            1
        } else {
            0
        }
        if (data[data.length - 2] == '=') {
            padding++
        }
        val result = ByteArray(data.length / 4 * 3 - padding)
        var d = 0
        var e = 0
        while (e < data.length) {
            val last = e + 4 >= data.length
            val c1: Char = data[e++]
            val c2: Char = data[e++]
            val c3: Char = data[e++]
            val c4: Char = data[e++]
            var tmp: Int = getByte(c1).toInt() shl 2 and 0xfc
            var tmp2: Int = getByte(c2).toInt() shr 4 and 0x03
            result[d++] = (tmp + tmp2).toByte()
            if (last && padding > 0) {
                if (padding == 1) {
                    tmp = getByte(c2).toInt() shl 4 and 0xf0
                    tmp2 = getByte(c3).toInt() shr 2 and 0x0f
                    result[d++] = (tmp + tmp2).toByte()
                }
            } else {
                tmp = getByte(c2).toInt() shl 4 and 0xf0
                tmp2 = getByte(c3).toInt() shr 2 and 0x0f
                result[d++] = (tmp + tmp2).toByte()
                tmp = getByte(c3).toInt() shl 6 and 0xc0
                tmp2 = getByte(c4).toInt() and 0x3f
                result[d++] = (tmp + tmp2).toByte()
            }
        }
        return result
    }

    private fun getByte(c: Char): Byte {
        return when (c) {
            in 'A'..'Z' -> {
                (c.code - 'A'.code and 0xff).toByte()
            }
            in 'a'..'z' -> {
                (((26 + c.code) - 'a'.code) and 0xff).toByte()
            }
            in '0'..'9' -> {
                (52 + c.code - '0'.code and 0xff).toByte()
            }
            '+', '-' -> {
                (62 and 0xff).toByte()
            }
            else -> { //if (c == '/' || c == '_') {
                (63 and 0xff).toByte()
            }
        }
    }
}