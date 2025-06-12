package com.snc.zero.crypto.encoder.base62

import java.io.ByteArrayOutputStream
import kotlin.math.ceil
import kotlin.math.ln

/**
 * Base62 from String
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
object Base62 {

    private const val STANDARD_BASE = 256

    private const val TARGET_BASE = 62

    private lateinit var lookup: ByteArray

    private lateinit var alphabet: ByteArray

    private fun createLookupTable() {
        lookup = ByteArray(256)
        for (i in alphabet.indices) {
            lookup[alphabet[i].toInt()] = (i and 0xFF).toByte()
        }
    }

    private fun translate(indices: ByteArray, dictionary: ByteArray): ByteArray {
        val translation = ByteArray(indices.size)
        for (i in indices.indices) {
            translation[i] = dictionary[indices[i].toInt()]
        }
        return translation
    }

    private fun estimateOutputLength(inputLength: Int, sourceBase: Int, targetBase: Int): Int {
        return ceil(ln(sourceBase.toDouble()) / ln(targetBase.toDouble()) * inputLength).toInt()
    }

    /**
     * This algorithm is inspired by: http://codegolf.stackexchange.com/a/21672
     */
    private fun convert(message: ByteArray, sourceBase: Int, targetBase: Int): ByteArray {
        val estimatedLength: Int = estimateOutputLength(message.size, sourceBase, targetBase)
        val out = ByteArrayOutputStream(estimatedLength)
        var source = message
        while (source.isNotEmpty()) {
            val quotient = ByteArrayOutputStream(source.size)
            var remainder = 0
            for (b in source) {
                val accumulator = (b.toInt() and 0xFF) + (remainder * sourceBase)
                val digit = (accumulator - (accumulator % targetBase)) / targetBase
                remainder = accumulator % targetBase
                if (quotient.size() > 0 || digit > 0) {
                    quotient.write(digit)
                }
            }
            out.write(remainder)
            source = quotient.toByteArray()
        }

        var i = 0
        while (i < message.size - 1 && message[i].toInt() == 0) {
            out.write(0)
            i++
        }
        return reverse(out.toByteArray())
    }

    private fun reverse(arr: ByteArray): ByteArray {
        val length = arr.size
        val reversed = ByteArray(length)
        for (i in 0 until length) {
            reversed[length - i - 1] = arr[i]
        }
        return reversed
    }

    fun isBase62Encoding(bytes: ByteArray?): Boolean {
        if (bytes == null) {
            return false
        }
        for (e in bytes) {
            if (('0'.code.toByte() > e || '9'.code.toByte() < e) &&
                ('a'.code.toByte() > e || 'z'.code.toByte() < e) &&
                ('A'.code.toByte() > e || 'Z'.code.toByte() < e)) {
                return false
            }
        }
        return true
    }

    object CharacterSets {
        val GMP = byteArrayOf(
            '0'.code.toByte(), '1'.code.toByte(), '2'.code.toByte(), '3'.code.toByte(),
            '4'.code.toByte(), '5'.code.toByte(), '6'.code.toByte(), '7'.code.toByte(),
            '8'.code.toByte(), '9'.code.toByte(), 'A'.code.toByte(), 'B'.code.toByte(),
            'C'.code.toByte(), 'D'.code.toByte(), 'E'.code.toByte(), 'F'.code.toByte(),
            'G'.code.toByte(), 'H'.code.toByte(), 'I'.code.toByte(), 'J'.code.toByte(),
            'K'.code.toByte(), 'L'.code.toByte(), 'M'.code.toByte(), 'N'.code.toByte(),
            'O'.code.toByte(), 'P'.code.toByte(), 'Q'.code.toByte(), 'R'.code.toByte(),
            'S'.code.toByte(), 'T'.code.toByte(), 'U'.code.toByte(), 'V'.code.toByte(),
            'W'.code.toByte(), 'X'.code.toByte(), 'Y'.code.toByte(), 'Z'.code.toByte(),
            'a'.code.toByte(), 'b'.code.toByte(), 'c'.code.toByte(), 'd'.code.toByte(),
            'e'.code.toByte(), 'f'.code.toByte(), 'g'.code.toByte(), 'h'.code.toByte(),
            'i'.code.toByte(), 'j'.code.toByte(), 'k'.code.toByte(), 'l'.code.toByte(),
            'm'.code.toByte(), 'n'.code.toByte(), 'o'.code.toByte(), 'p'.code.toByte(),
            'q'.code.toByte(), 'r'.code.toByte(), 's'.code.toByte(), 't'.code.toByte(),
            'u'.code.toByte(), 'v'.code.toByte(), 'w'.code.toByte(), 'x'.code.toByte(),
            'y'.code.toByte(), 'z'.code.toByte()
        )
        val INVERTED = byteArrayOf(
            '0'.code.toByte(), '1'.code.toByte(), '2'.code.toByte(), '3'.code.toByte(),
            '4'.code.toByte(), '5'.code.toByte(), '6'.code.toByte(), '7'.code.toByte(),
            '8'.code.toByte(), '9'.code.toByte(), 'a'.code.toByte(), 'b'.code.toByte(),
            'c'.code.toByte(), 'd'.code.toByte(), 'e'.code.toByte(), 'f'.code.toByte(),
            'g'.code.toByte(), 'h'.code.toByte(), 'i'.code.toByte(), 'j'.code.toByte(),
            'k'.code.toByte(), 'l'.code.toByte(), 'm'.code.toByte(), 'n'.code.toByte(),
            'o'.code.toByte(), 'p'.code.toByte(), 'q'.code.toByte(), 'r'.code.toByte(),
            's'.code.toByte(), 't'.code.toByte(), 'u'.code.toByte(), 'v'.code.toByte(),
            'w'.code.toByte(), 'x'.code.toByte(), 'y'.code.toByte(), 'z'.code.toByte(),
            'A'.code.toByte(), 'B'.code.toByte(), 'C'.code.toByte(), 'D'.code.toByte(),
            'E'.code.toByte(), 'F'.code.toByte(), 'G'.code.toByte(), 'H'.code.toByte(),
            'I'.code.toByte(), 'J'.code.toByte(), 'K'.code.toByte(), 'L'.code.toByte(),
            'M'.code.toByte(), 'N'.code.toByte(), 'O'.code.toByte(), 'P'.code.toByte(),
            'Q'.code.toByte(), 'R'.code.toByte(), 'S'.code.toByte(), 'T'.code.toByte(),
            'U'.code.toByte(), 'V'.code.toByte(), 'W'.code.toByte(), 'X'.code.toByte(),
            'Y'.code.toByte(), 'Z'.code.toByte()
        )
    }

    fun encode(message: ByteArray, alphabet: ByteArray = CharacterSets.GMP): ByteArray {
        Base62.alphabet = alphabet
        createLookupTable()
        val indices: ByteArray = convert(message, STANDARD_BASE, TARGET_BASE)
        return translate(indices, alphabet)
    }

    fun decode(encoded: ByteArray, alphabet: ByteArray = CharacterSets.GMP): ByteArray {
        require(isBase62Encoding(encoded)) { "Input is not encoded correctly" }
        Base62.alphabet = alphabet
        createLookupTable()
        val prepared = translate(encoded, lookup)
        return convert(prepared, TARGET_BASE, STANDARD_BASE)
    }
}
