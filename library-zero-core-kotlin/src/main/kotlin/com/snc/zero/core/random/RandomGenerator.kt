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

        private val RANDOM = SecureRandom.getInstance("SHA1PRNG")

        fun random(count: Int, letters: Boolean = true, numbers: Boolean = true): String {
            return random(count, 0, 0, letters, numbers, null, RANDOM)
        }

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

        fun random(
            countLen: Int,
            startPos: Int,
            endPos: Int,
            letters: Boolean,
            numbers: Boolean,
            chars: CharArray? = null,
            random: Random = RANDOM
        ): String {
            require(countLen >= 0) { "Requested random string length $countLen is less than 0." }

            val (start, end) = determineCharRange(startPos, endPos, letters, numbers)
            val buffer = CharArray(countLen)
            var remainingCount = countLen

            var previousRemainingCount = remainingCount
            var sameCountIterations = 0

            while (remainingCount > 0) {
                val ch = generateRandomChar(chars, random, start, end)

                if (shouldIncludeChar(ch, letters, numbers)) {
                    remainingCount = handleCharacterPlacement(buffer, remainingCount, ch, random)
                }

                // 무한 루프 방지 로직
                if (remainingCount == previousRemainingCount) {
                    sameCountIterations++
                    check(sameCountIterations < 100) {
                        "무한 루프가 감지되었습니다. remainingCount가 100번 이상 동일한 값($remainingCount)을 유지하고 있습니다."
                    }
                } else {
                    // remainingCount가 변경되면 카운터 리셋
                    previousRemainingCount = remainingCount
                    sameCountIterations = 0
                }
            }
            return String(buffer)
        }

        private fun determineCharRange(
            startPos: Int,
            endPos: Int,
            letters: Boolean,
            numbers: Boolean
        ): Pair<Int, Int> {
            if (startPos != 0 || endPos != 0) {
                return startPos to endPos
            }

            return if (!letters && !numbers) {
                0 to Int.MAX_VALUE
            } else {
                32 to 123
            }
        }

        private fun generateRandomChar(
            chars: CharArray?,
            random: Random,
            start: Int,
            end: Int
        ): Char {
            val gap = end - start
            return if (chars == null) {
                (random.nextInt(gap) + start).toChar()
            } else {
                chars[random.nextInt(gap) + start]
            }
        }

        private fun shouldIncludeChar(ch: Char, letters: Boolean, numbers: Boolean): Boolean {
            return (letters && ch.isLetter()) ||
                (numbers && ch.isDigit()) ||
                (!letters && !numbers)
        }

        private fun handleCharacterPlacement(
            buffer: CharArray,
            count: Int,
            ch: Char,
            random: Random
        ): Int {
            return when (ch.code) {
                in 0xDC00..0xDFFF -> handleLowSurrogate(buffer, count, ch, random)
                in 0xD800..0xDB7F -> handleHighSurrogate(buffer, count, ch, random)
                in 0xDB80..0xDBFF -> count // Skip invalid surrogate range
                else -> {
                    buffer[count - 1] = ch
                    count - 1
                }
            }
        }

        private fun handleLowSurrogate(
            buffer: CharArray,
            count: Int,
            ch: Char,
            random: Random
        ): Int {
            if (count == 1) return count // Skip if only one position left

            buffer[count - 1] = ch
            buffer[count - 2] = (0xD800 + random.nextInt(128)).toChar()
            return count - 2
        }

        fun handleHighSurrogate(
            buffer: CharArray,
            count: Int,
            ch: Char,
            random: Random
        ): Int {
            if (count == 1) return count // Skip if only one position left

            buffer[count - 1] = (0xDC00 + random.nextInt(128)).toChar()
            buffer[count - 2] = ch
            return count - 2
        }
    }
}
