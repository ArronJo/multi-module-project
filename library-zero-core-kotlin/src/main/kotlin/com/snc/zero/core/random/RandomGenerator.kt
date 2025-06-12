package com.snc.zero.core.random

import java.security.SecureRandom
import java.util.Random

/**
 * 랜덤
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class RandomGenerator private constructor() {

    companion object {

        @JvmStatic
        private val RANDOM = SecureRandom.getInstance("SHA1PRNG")

        @JvmStatic
        fun random(count: Int, letters: Boolean = true, numbers: Boolean = true): String {
            return random(count, 0, 0, letters, numbers, null, RANDOM)
        }

        @JvmStatic
        fun randomNumber(count: Int): String {
            return random(
                count,
                startPos = 0,
                endPos = 0,
                letters = false,
                numbers = true,
                chars = null,
                random = RANDOM
            )
        }

        @JvmStatic
        fun randomKorean(count: Int): String {
            return random(
                count,
                startPos = 44032,
                endPos = 55203,
                letters = false,
                numbers = false,
                chars = null,
                random = RANDOM
            )
        }

        @JvmStatic
        fun random(
            countLen: Int,
            startPos: Int,
            endPos: Int,
            letters: Boolean,
            numbers: Boolean,
            chars: CharArray? = null,
            random: Random = RANDOM
        ): String {
            var start = startPos
            var end = endPos
            var count = countLen
            if (count == 0) return ""
            require(count >= 0) { "Requested random string length $count is less than 0." }

            if (start == 0 && end == 0) {
                end = 123
                start = 32
                if (!letters && !numbers) {
                    start = 0
                    end = Int.MAX_VALUE
                }
            }

            val buffer = CharArray(count)
            val gap = end - start

            while (count != 0) {
                val ch: Char = if (chars == null) {
                    (random.nextInt(gap) + start).toChar()
                } else {
                    chars[random.nextInt(gap) + start]
                }

                if ((letters && ch.isLetter()) || (numbers && ch.isDigit()) || (!letters && !numbers)) {
                    when (ch.code) {
                        in 0xDC00..0xDFFF -> { // Low surrogate
                            if (count == 1) {
                                continue // skip and try again
                            }
                            buffer[--count] = ch
                            buffer[--count] = (0xD800 + random.nextInt(128)).toChar()
                        }

                        in 0xD800..0xDB7F -> { // High surrogate
                            if (count == 1) {
                                continue
                            }
                            buffer[--count] = (0xDC00 + random.nextInt(128)).toChar()
                            buffer[--count] = ch
                        }

                        in 0xDB80..0xDBFF -> { // Invalid surrogate range
                            continue
                        }

                        else -> {
                            buffer[--count] = ch
                        }
                    }
                } else {
                    // character did not match letter/digit/non-filter rule → skip
                    continue
                }
            }

            return String(buffer)
        }
    }
}
