package com.snc.zero.hangul.engtokor

/**
 * https://www.theyt.net/wiki/%ED%95%9C%EC%98%81%ED%83%80%EB%B3%80%ED%99%98%EA%B8%B0
 * 복잡도가 너무 높아서 SonarQube 에서 지적을 당해서 Claude 로 Refactoring 하였다.
 */
class EngToKor {

    companion object {
        private val engKeys = "rRseEfaqQtTdwWczxvgkoiOjpuPhynbml"
        private val korKeys = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅛㅜㅠㅡㅣ"
        private val choData = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
        private val jungData = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"
        private val jongData = "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ"

        fun engToKor(src: String): String {
            if (src.isEmpty()) return ""

            val result = StringBuilder()
            val hangulState = HangulState()

            for (ch in src) {
                val keyIndex = engKeys.indexOf(ch)

                when {
                    keyIndex == -1 -> processNonKoreanChar(ch, result, hangulState)
                    keyIndex < 19 -> processConsonant(keyIndex, result, hangulState)
                    else -> processVowel(keyIndex, result, hangulState)
                }
            }

            // 마지막 한글 처리
            result.append(getFinalHangul(hangulState))

            return result.toString()
        }

        private data class HangulState(
            var nCho: Int = -1,
            var nJung: Int = -1,
            var nJong: Int = -1
        ) {
            fun reset() {
                nCho = -1
                nJung = -1
                nJong = -1
            }
        }

        private fun processNonKoreanChar(ch: Char, result: StringBuilder, state: HangulState) {
            result.append(getFinalHangul(state))
            state.reset()
            result.append(ch)
        }

        private fun getFinalHangul(state: HangulState): String {
            return when {
                state.nCho != -1 -> {
                    if (state.nJung != -1) {
                        makeHangul(state.nCho, state.nJung, state.nJong).toString()
                    } else {
                        choData[state.nCho].toString()
                    }
                }

                state.nJung != -1 -> jungData[state.nJung].toString()
                state.nJong != -1 -> jongData[state.nJong].toString()
                else -> ""
            }
        }

        private fun processConsonant(keyIndex: Int, result: StringBuilder, state: HangulState) {
            when {
                state.nJung != -1 -> processConsonantAfterVowel(keyIndex, result, state)
                else -> processConsonantInitial(keyIndex, result, state)
            }
        }

        private fun processConsonantAfterVowel(keyIndex: Int, result: StringBuilder, state: HangulState) {
            if (state.nCho == -1) {
                // 중성만 입력됨, 초성으로 전환
                result.append(jungData[state.nJung])
                state.nJung = -1
                state.nCho = choData.indexOf(korKeys[keyIndex])
            } else {
                processJongSeong(keyIndex, result, state)
            }
        }

        private fun processJongSeong(keyIndex: Int, result: StringBuilder, state: HangulState) {
            if (state.nJong == -1) {
                val jongIndex = jongData.indexOf(korKeys[keyIndex])
                if (jongIndex == -1) {
                    // 종성이 아니라 초성
                    result.append(makeHangul(state.nCho, state.nJung, state.nJong))
                    state.nCho = choData.indexOf(korKeys[keyIndex])
                    state.nJung = -1
                } else {
                    state.nJong = jongIndex
                }
            } else {
                val newJongIndex = getCombinedJongIndex(state.nJong, keyIndex)
                if (newJongIndex != -1) {
                    state.nJong = newJongIndex
                } else {
                    // 종성 입력 끝, 새로운 초성으로
                    result.append(makeHangul(state.nCho, state.nJung, state.nJong))
                    state.nCho = choData.indexOf(korKeys[keyIndex])
                    state.nJung = -1
                    state.nJong = -1
                }
            }
        }

        private fun processConsonantInitial(keyIndex: Int, result: StringBuilder, state: HangulState) {
            if (state.nCho == -1) {
                if (state.nJong != -1) {
                    result.append(jongData[state.nJong])
                    state.nJong = -1
                }
                state.nCho = choData.indexOf(korKeys[keyIndex])
            } else {
                val combinedJongIndex = getCombinedJongIndex(state.nCho, keyIndex)
                if (combinedJongIndex != -1) {
                    state.nCho = -1
                    state.nJong = combinedJongIndex
                } else {
                    // 단자음 연타
                    result.append(choData[state.nCho])
                    state.nCho = choData.indexOf(korKeys[keyIndex])
                }
            }
        }

        private fun processVowel(keyIndex: Int, result: StringBuilder, state: HangulState) {
            if (state.nJong != -1) {
                processVowelAfterJong(keyIndex, result, state)
            } else {
                processVowelNormal(keyIndex, result, state)
            }
        }

        private fun processVowelAfterJong(keyIndex: Int, result: StringBuilder, state: HangulState) {
            // 복자음 분해 또는 단일 종성을 초성으로 이동
            val (remainingJong, newCho) = decomposeJongSeong(state.nJong)

            // 앞 글자 완성
            if (state.nCho != -1) {
                result.append(makeHangul(state.nCho, state.nJung, remainingJong))
            } else if (remainingJong != -1) {
                result.append(jongData[remainingJong])
            }

            // 새로운 음절 시작
            state.nCho = newCho
            state.nJung = jungData.indexOf(korKeys[keyIndex])
            state.nJong = -1
        }

        private fun processVowelNormal(keyIndex: Int, result: StringBuilder, state: HangulState) {
            if (state.nJung == -1) {
                state.nJung = jungData.indexOf(korKeys[keyIndex])
            } else {
                val combinedJungIndex = getCombinedJungIndex(state.nJung, keyIndex)
                if (combinedJungIndex != -1) {
                    state.nJung = combinedJungIndex
                } else {
                    // 조합 안되는 모음
                    if (state.nCho != -1) {
                        result.append(makeHangul(state.nCho, state.nJung, state.nJong))
                        state.nCho = -1
                    } else {
                        result.append(jungData[state.nJung])
                    }
                    state.nJung = -1
                    result.append(korKeys[keyIndex])
                }
            }
        }

        private fun getCombinedJongIndex(baseIndex: Int, keyIndex: Int): Int {
            return when {
                baseIndex == 0 && keyIndex == 9 -> 2 // ㄳ
                baseIndex == 3 && keyIndex == 12 -> 4 // ㄵ
                baseIndex == 3 && keyIndex == 18 -> 5 // ㄶ
                baseIndex == 7 && keyIndex == 0 -> 8 // ㄺ
                baseIndex == 7 && keyIndex == 6 -> 9 // ㄻ
                baseIndex == 7 && keyIndex == 7 -> 10 // ㄼ
                baseIndex == 7 && keyIndex == 9 -> 11 // ㄽ
                baseIndex == 7 && keyIndex == 16 -> 12 // ㄾ
                baseIndex == 7 && keyIndex == 17 -> 13 // ㄿ
                baseIndex == 7 && keyIndex == 18 -> 14 // ㅀ
                baseIndex == 16 && keyIndex == 9 -> 17 // ㅄ
                else -> -1
            }
        }

        private fun getCombinedJungIndex(baseIndex: Int, keyIndex: Int): Int {
            return when {
                baseIndex == 8 && keyIndex == 19 -> 9 // ㅘ
                baseIndex == 8 && keyIndex == 20 -> 10 // ㅙ
                baseIndex == 8 && keyIndex == 32 -> 11 // ㅚ
                baseIndex == 13 && keyIndex == 23 -> 14 // ㅝ
                baseIndex == 13 && keyIndex == 24 -> 15 // ㅞ
                baseIndex == 13 && keyIndex == 32 -> 16 // ㅟ
                baseIndex == 18 && keyIndex == 32 -> 19 // ㅢ
                else -> -1
            }
        }

        private fun decomposeJongSeong(jongIndex: Int): Pair<Int, Int> {
            return when (jongIndex) {
                2 -> Pair(0, 9) // ㄳ -> ㄱ, ㅅ
                4 -> Pair(3, 12) // ㄵ -> ㄴ, ㅈ
                5 -> Pair(3, 18) // ㄶ -> ㄴ, ㅎ
                8 -> Pair(7, 0) // ㄺ -> ㄹ, ㄱ
                9 -> Pair(7, 6) // ㄻ -> ㄹ, ㅁ
                10 -> Pair(7, 7) // ㄼ -> ㄹ, ㅂ
                11 -> Pair(7, 9) // ㄽ -> ㄹ, ㅅ
                12 -> Pair(7, 16) // ㄾ -> ㄹ, ㅌ
                13 -> Pair(7, 17) // ㄿ -> ㄹ, ㅍ
                14 -> Pair(7, 18) // ㅀ -> ㄹ, ㅎ
                17 -> Pair(16, 9) // ㅄ -> ㅂ, ㅅ
                else -> {
                    // 단일 종성의 경우, 해당 종성을 초성으로 변환
                    val choIndex = choData.indexOf(jongData[jongIndex])
                    Pair(-1, choIndex)
                }
            }
        }

        private fun makeHangul(nCho: Int, nJung: Int, nJong: Int): Char {
            return (0xAC00 + nCho * 21 * 28 + nJung * 28 + nJong + 1).toChar()
        }
    }
}
