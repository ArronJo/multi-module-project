package com.snc.zero.hangul.engtokor

/**
 * https://www.theyt.net/wiki/%ED%95%9C%EC%98%81%ED%83%80%EB%B3%80%ED%99%98%EA%B8%B0
 */
class EngToKor {

    private var engKeys = "rRseEfaqQtTdwWczxvgkoiOjpuPhynbml"
    private var korKeys = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅛㅜㅠㅡㅣ"
    private var choData = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
    private var jungData = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"
    private var jongData = "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ"

    fun en2ko(src: String): String {
        var res = ""
        if (src.isEmpty()) return res

        var nCho = -1
        var nJung = -1
        var nJong = -1

        for (ch in src) {
            val p = engKeys.indexOf(ch)
            if (p == -1) {
                // 영자판이 아님
                // 남아있는 한글이 있으면 처리
                if (nCho != -1) {
                    res += if (nJung != -1) { // 초성+중성+(종성)
                        makeHangul(nCho, nJung, nJong)
                    } else { // 초성만
                        choData[nCho]
                    }
                } else {
                    if (nJung != -1) { // 중성만
                        res += jungData[nJung]
                    } else if (nJong != -1) { // 복자음
                        res += jongData[nJong]
                    }
                }
                nCho = -1
                nJung = -1
                nJong = -1
                res += ch
            } else if (p < 19) { // 자음
                if (nJung != -1) {
                    if (nCho == -1) { // 중성만 입력됨, 초성으로
                        res += jungData[nJung]
                        nJung = -1
                        nCho = choData.indexOf(korKeys[p])
                    } else { // 종성이다
                        if (nJong == -1) { // 종성 입력 중
                            nJong = jongData.indexOf(korKeys[p])
                            if (nJong == -1) { // 종성이 아니라 초성이다
                                res += makeHangul(nCho, nJung, nJong)
                                nCho = choData.indexOf(korKeys[p])
                                nJung = -1
                            }
                        } else if (nJong == 0 && p == 9) { // ㄳ
                            nJong = 2
                        } else if (nJong == 3 && p == 12) { // ㄵ
                            nJong = 4
                        } else if (nJong == 3 && p == 18) { // ㄶ
                            nJong = 5
                        } else if (nJong == 7 && p == 0) { // ㄺ
                            nJong = 8
                        } else if (nJong == 7 && p == 6) { // ㄻ
                            nJong = 9
                        } else if (nJong == 7 && p == 7) { // ㄼ
                            nJong = 10
                        } else if (nJong == 7 && p == 9) { // ㄽ
                            nJong = 11
                        } else if (nJong == 7 && p == 16) { // ㄾ
                            nJong = 12
                        } else if (nJong == 7 && p == 17) { // ㄿ
                            nJong = 13
                        } else if (nJong == 7 && p == 18) { // ㅀ
                            nJong = 14
                        } else if (nJong == 16 && p == 9) { // ㅄ
                            nJong = 17
                        } else { // 종성 입력 끝, 초성으로
                            res += makeHangul(nCho, nJung, nJong)
                            nCho = choData.indexOf(korKeys[p])
                            nJung = -1
                            nJong = -1
                        }
                    }
                } else { // 초성 또는 (단/복)자음이다
                    if (nCho == -1) { // 초성 입력 시작
                        if (nJong != -1) { // 복자음 후 초성
                            res += jongData[nJong]
                            nJong = -1
                        }
                        nCho = choData.indexOf(korKeys[p])
                    } else if (nCho == 0 && p == 9) { // ㄳ
                        nCho = -1
                        nJong = 2
                    } else if (nCho == 2 && p == 12) { // ㄵ
                        nCho = -1
                        nJong = 4
                    } else if (nCho == 2 && p == 18) { // ㄶ
                        nCho = -1
                        nJong = 5
                    } else if (nCho == 5 && p == 0) { // ㄺ
                        nCho = -1
                        nJong = 8
                    } else if (nCho == 5 && p == 6) { // ㄻ
                        nCho = -1
                        nJong = 9
                    } else if (nCho == 5 && p == 7) { // ㄼ
                        nCho = -1
                        nJong = 10
                    } else if (nCho == 5 && p == 9) { // ㄽ
                        nCho = -1
                        nJong = 11
                    } else if (nCho == 5 && p == 16) { // ㄾ
                        nCho = -1
                        nJong = 12
                    } else if (nCho == 5 && p == 17) { // ㄿ
                        nCho = -1
                        nJong = 13
                    } else if (nCho == 5 && p == 18) { // ㅀ
                        nCho = -1
                        nJong = 14
                    } else if (nCho == 7 && p == 9) { // ㅄ
                        nCho = -1
                        nJong = 17
                    } else { // 단자음을 연타
                        res += choData[nCho]
                        nCho = choData.indexOf(korKeys[p])
                    }
                }
            } else { // 모음
                if (nJong != -1) { // (앞글자 종성), 초성+중성
                    // 복자음 다시 분해
                    var newCho: Int // (임시용) 초성
                    when (nJong) {
                        2 -> { // ㄱ, ㅅ
                            nJong = 0
                            newCho = 9
                        }
                        4 -> { // ㄴ, ㅈ
                            nJong = 3
                            newCho = 12
                        }
                        5 -> { // ㄴ, ㅎ
                            nJong = 3
                            newCho = 18
                        }
                        8 -> { // ㄹ, ㄱ
                            nJong = 7
                            newCho = 0
                        }
                        9 -> { // ㄹ, ㅁ
                            nJong = 7
                            newCho = 6
                        }
                        10 -> { // ㄹ, ㅂ
                            nJong = 7
                            newCho = 7
                        }
                        11 -> { // ㄹ, ㅅ
                            nJong = 7
                            newCho = 9
                        }
                        12 -> { // ㄹ, ㅌ
                            nJong = 7
                            newCho = 16
                        }
                        13 -> { // ㄹ, ㅍ
                            nJong = 7
                            newCho = 17
                        }
                        14 -> { // ㄹ, ㅎ
                            nJong = 7
                            newCho = 18
                        }
                        17 -> { // ㅂ, ㅅ
                            nJong = 16
                            newCho = 9
                        }
                        else -> { // 복자음 아님
                            newCho = choData.indexOf(jongData[nJong])
                            nJong = -1
                        }
                    }
                    res += if (nCho != -1) { // 앞글자가 초성+중성+(종성)
                        makeHangul(nCho, nJung, nJong)
                    } else { // 복자음만 있음
                        jongData[nJong]
                    }

                    nCho = newCho
                    nJung = -1
                    nJong = -1
                }
                if (nJung == -1) { // 중성 입력 중
                    nJung = jungData.indexOf(korKeys[p])
                } else if (nJung == 8 && p == 19) { // ㅘ
                    nJung = 9
                } else if (nJung == 8 && p == 20) { // ㅙ
                    nJung = 10
                } else if (nJung == 8 && p == 32) { // ㅚ
                    nJung = 11
                } else if (nJung == 13 && p == 23) { // ㅝ
                    nJung = 14
                } else if (nJung == 13 && p == 24) { // ㅞ
                    nJung = 15
                } else if (nJung == 13 && p == 32) { // ㅟ
                    nJung = 16
                } else if (nJung == 18 && p == 32) { // ㅢ
                    nJung = 19
                } else { // 조합 안되는 모음 입력
                    if (nCho != -1) { // 초성+중성 후 중성
                        res += makeHangul(nCho, nJung, nJong)
                        nCho = -1
                    } else { // 중성 후 중성
                        res += jungData[nJung]
                    }
                    nJung = -1
                    res += korKeys[p]
                }
            }
        }

        // 마지막 한글이 있으면 처리
        if (nCho != -1) {
            res += if (nJung != -1) { // 초성+중성+(종성)
                makeHangul(nCho, nJung, nJong)
            } else { // 초성만
                choData[nCho]
            }
        } else {
            if (nJung != -1) { // 중성만
                res += jungData[nJung]
            } else { // 복자음
                if (nJong != -1) {
                    res += jongData[nJong]
                }
            }
        }

        return res
    }

    private fun makeHangul(nCho: Int, nJung: Int, nJong: Int): Char {
        return (0xAC00 + nCho * 21 * 28 + nJung * 28 + nJong + 1).toChar()
    }
}
