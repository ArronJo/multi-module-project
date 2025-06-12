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
            if (count == 0) {
                return ""
            }
            require(count >= 0) { "Requested random string length $count is less than 0." }
            if (start == 0 && end == 0) {
                end = 123
                start = 32
                if (!letters && !numbers) {
                    start = 0
                    end = 2147483647
                }
            }

            val buffer = CharArray(count)
            val gap = end - start
            while (count-- != 0) {
                val ch: Char = if (chars == null) {
                    (random.nextInt(gap) + start).toChar()
                } else {
                    chars[random.nextInt(gap) + start]
                }
                if ((letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
                    if (ch.code in 56320..57343) if (count == 0) {
                        ++count
                    } else {
                        buffer[count] = ch
                        --count
                        buffer[count] = (55296 + random.nextInt(128)).toChar()
                    } else if (ch.code in 55296..56191) if (count == 0) {
                        ++count
                    } else {
                        buffer[count] = (56320 + random.nextInt(128)).toChar()
                        --count
                        buffer[count] = ch
                    } else if (ch.code in 56192..56319) {
                        ++count
                    } else {
                        buffer[count] = ch
                    }
                } else {
                    ++count
                }
            }
            return String(buffer)
        }
    }
}
