package com.snc.test.similarity.matcher

import com.snc.zero.extensions.text.removeAllWhitespace
import com.snc.zero.similarity.data.InMemoryStringDataProvider
import com.snc.zero.similarity.matcher.StringSimilarityMatcher
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class StringSimilarityMatcherTest : BaseJUnit5Test() {

    private var data: List<String> = listOf(
        "salary",
        "mysalard",
        "mysalaygood",
        "sadary",
        "payback",
        "laptop",
        "notebook",
        "sblbry",
        "wowsblarywow",
        "과메기",
        "괄자",
        "과리자",
        "관라자",
        "관리자",
        "관리",
        "보험 계약정보",
        "보험계약 조회",
        "나의 자동이체정보",
        "미래연금예상액 조회",
        "보험금신",
        "사고보험금 신청",
        "사고보험금 내역조회",
        "사고보험금 서류보완",
        "일반보험금 신청",
        "일반보험금 지급내역",
        "보험금납입",
        "보험료 조회/납입",
        "납입계좌 변경",
        "보험계약변경",
        "건강체변경 신청",
        "보험수익자 변경 신청",
        "감액/감액완납 신청",
        "펀드변경 신청",
        "보험청약관리",
        "신계약모니터링",
        "청약 보완처리",
        "건강검진결과 제출",
        "청약 철회 신청/취소",
        "증명서/확인서",
        "안내장 발송내역",
        "대출관리",
        "보험계약대출 거래내역",
        "보험계약대출 관리",
        "보험계약대출 상환",
        "변액보험계약대출 계정관리",
        "신용담보대출 관리",
        "퇴직연금 관리",
        "퇴직연금 계약조회",
        "적립금 조회",
        "수익률 조회",
        "적립금 운용현황 조회/변경",
        "부담금 투입비율 조회/변경",
        "개인형IRP지급청구",
        "개인형IRP 계약해지(일시금수령)",
        "개인형IRP 연금수령신청",
        "개인형IRP 타사로 계약이전",
        "개인형IRP 공지사항",
        "개인형IRP 펀드정보 조회"
    )

    private val target = "계약" // 검색어

    @Nested
    inner class Similarity {

        val matcher = StringSimilarityMatcher()
        val minSimilarity = 0.3 // 30% 이상만 출력하도록 조건

        @Test
        fun `레벤슈타인 거리(Levenshtein Distance) 유사도 테스트`() {
            println("=== 레벤슈타인 유사도 (최소 $minSimilarity) 출력 ===")
            println("-Usage: findSimilarStrings( ..., SimilarityMethod.LEVENSHTEIN )")

            println("검색어: $target")
            println()

            val levenshteinResults = matcher.findSimilarStrings(
                data,
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.LEVENSHTEIN
            )
            levenshteinResults.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }

            println("=== 내부 데이터 사용 ===")
            val levenshteinResults2 = matcher.findSimilarStrings(
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.LEVENSHTEIN
            )
            levenshteinResults2.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }
            println()
        }

        @Test
        fun `자카드 유사도(Jaccard Similarity) 유사도 테스트`() {
            println("=== 자카드 유사도 (최소 $minSimilarity) 출력 ===")
            println("-Usage: findSimilarStrings( ..., SimilarityMethod.JACCARD )")

            println("검색어: $target")
            println()

            val jaccardResults = matcher.findSimilarStrings(
                data,
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.JACCARD
            )
            jaccardResults.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }
            println()
        }

        @Test
        fun `코사인 유사도(Cosine Similarity) 유사도 테스트`() {
            println("=== 코사인 유사도 (최소 $minSimilarity) 출력 ===")
            println("-Usage: findSimilarStrings( ..., SimilarityMethod.COSINE )")

            println("검색어: $target")
            println()

            val cosineResults = matcher.findSimilarStrings(
                data,
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.COSINE
            )
            cosineResults.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }
            println()
        }

        @Test
        fun `자로 유사도(Jaro Similarity) 유사도 테스트`() {
            println("=== 자로 유사도 (최소 $minSimilarity) 출력 ===")
            println("-Usage: findSimilarStrings( ..., SimilarityMethod.JARO )")

            println("검색어: $target")
            println()

            val cosineResults = matcher.findSimilarStrings(
                data,
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.JARO
            )
            cosineResults.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }
            println()
        }

        @Test
        fun `자로 웡클러 유사도(Jaro Winkler Similarity) 유사도 테스트`() {
            println("=== 자로 웡클러 유사도 (최소 $minSimilarity) 출력 ===")
            println("-Usage: findSimilarStrings( ..., SimilarityMethod.JARO_WINKLER )")

            println("검색어: $target")
            println()

            val cosineResults = matcher.findSimilarStrings(
                data,
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.JARO_WINKLER
            )
            cosineResults.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }
            println()
        }

        @Test
        fun `자로 and 자로 웡클러 유사도(Jaro Winkler Similarity) 유사도 테스트`() {
            val matcher = StringSimilarityMatcher()

            // 공통 접두사가 없는 경우
            val s1 = "계약"
            val s2 = "보험계약 조회"
            println("Jaro: ${matcher.jaroSimilarity(s1, s2)}")
            println("Jaro-Winkler: ${matcher.jaroWinklerSimilarity(s1, s2)}")
            println("Jaro-Winkler tip: ${matcher.jaroWinklerSimilarity(s1, s2.removeAllWhitespace())}")

            // 공통 접두사가 없는 경우
            val s7 = "약서"
            val s8 = "계약서"
            println("\nJaro: ${matcher.jaroSimilarity(s7, s8)}")
            println("Jaro-Winkler (threshold=0.5, scale=0.25): ${matcher.jaroWinklerSimilarity(s7, s8,0.5, 0.25)}")
            println("Jaro-Winkler (threshold=0.7, scale=0.1): ${matcher.jaroWinklerSimilarity(s7, s8, 0.7, 0.1)}")

            // 공통 접두사가 있는 경우로 테스트
            val s3 = "계약"
            val s4 = "계약서"
            println("\nJaro: ${matcher.jaroSimilarity(s3, s4)}")
            println("Jaro-Winkler (threshold=0.5, scale=0.25): ${matcher.jaroWinklerSimilarity(s3, s4, 0.5, 0.25)}")
            println("Jaro-Winkler (threshold=0.7, scale=0.1): ${matcher.jaroWinklerSimilarity(s3, s4, 0.7, 0.1)}")

            // 영어 예시 (더 명확한 차이)
            val s5 = "John"
            val s6 = "Johnson"
            println("\nJaro: ${matcher.jaroSimilarity(s5, s6)}")
            println("Jaro-Winkler: ${matcher.jaroWinklerSimilarity(s5, s6)}")
        }
    }

    @Nested
    inner class FindByDifferentCharCount {
        val matcher = StringSimilarityMatcher(InMemoryStringDataProvider())

        @BeforeEach
        fun setUp() {
            matcher.clearData()
            matcher.addData(data)
        }

        @Test
        fun `(빈도 기반) 다른 알파벳 개수 기준 테스트 1`() {
            println("=== 빈도 기반 차이 계산 테스트 1 ===")
            println("-Usage: findByDifferentCharCount()")

            println("타겟: '$target'")
            println()

            println("=== (빈도 기반) 다른 알파벳 개수 기준 (최대 4개) ===")
            val frequencyResults1 = matcher.findByDifferentCharCount(data, target, 4)
            frequencyResults1.forEach { (text, diffCount) ->
                println("  $text: $diffCount 개 차이")
                when (text) {
                    "salary" -> assertEquals(diffCount, 0)
                    "mysalard" -> assertEquals(diffCount, 2)
                    "sadary" -> assertEquals(diffCount, 2)
                }
            }
            println()

            println("=== (빈도 기반) 다른 알파벳 개수 기준 (최대 3개) ===")
            val frequencyResults3 = matcher.findByDifferentCharCount(data, target)
            frequencyResults3.forEach { (text, diffCount) ->
                println("  $text: $diffCount 개 차이")
                when (text) {
                    "salary" -> assertEquals(diffCount, 0)
                    "mysalard" -> assertEquals(diffCount, 2)
                    "sadary" -> assertEquals(diffCount, 2)
                    "sblbry" -> assertEquals(diffCount, 4)
                }
            }
            println()

            println("=== (빈도 기반) 다른 알파벳 개수 기준 (최대 1개) ===")
            val frequencyResults2 = matcher.findByDifferentCharCount(data, target, 1)
            frequencyResults2.forEach { (text, diffCount) ->
                println("$text: 다른 문자 $diffCount 개")
                when (text) {
                    "salary" -> assertEquals(diffCount, 0)
                }
            }
            println()

            println("=== (빈도 기반) 다른 알파벳 개수 기준 (내부 데이터 사용) ===")
            val frequencyResultsInner = matcher.findByDifferentCharCount(target, 4)
            frequencyResultsInner.forEach { (text, diffCount) ->
                println("  $text: $diffCount 개 차이")
                when (text) {
                    "salary" -> assertEquals(diffCount, 0)
                    "mysalard" -> assertEquals(diffCount, 2)
                    "sadary" -> assertEquals(diffCount, 2)
                }
            }
            println()
        }

        @Test
        fun `(빈도 기반) 다른 알파벳 개수 기준 테스트 2`() {
            println("=== 빈도 기반 차이 계산 테스트 2 ===")
            println("-Usage: findMatchingStrings( ..., DifferenceMethod.FREQUENCY_BASED )")

            println("타겟: '$target'")
            println()

            println("=== (빈도 기반) 다른 알파벳 개수 기준 (최대 5개) ===")
            val substringResults = matcher.findMatchingStrings(
                data = data,
                target = target,
                maxDifferences = 5,
                includeSubstring = false,
                method = StringSimilarityMatcher.DifferenceMethod.FREQUENCY_BASED
            )

            substringResults.forEach { result ->
                println("${result.originalString} -> ${result.matchedPart} (차이: ${result.differences}개)")
                when (result.originalString) {
                    "salary" -> assertEquals(result.differences, 0)
                    "sadary" -> assertEquals(result.differences, 2)
                    "sblbry" -> assertEquals(result.differences, 4)
                }
            }
            println()

            println("=== (빈도 기반) 다른 알파벳 개수 기준 (내부 데이터 사용) ===")
            val substringResults2 = matcher.findMatchingStrings(
                target = target,
                maxDifferences = 5,
                includeSubstring = false,
                method = StringSimilarityMatcher.DifferenceMethod.FREQUENCY_BASED
            )

            substringResults2.forEach { result ->
                println("${result.originalString} -> ${result.matchedPart} (차이: ${result.differences}개)")
                when (result.originalString) {
                    "salary" -> assertEquals(result.differences, 0)
                    "sadary" -> assertEquals(result.differences, 2)
                    "sblbry" -> assertEquals(result.differences, 4)
                }
            }
            println()
        }
    }

    @Nested
    inner class FindByPositionDifferences {
        val matcher = StringSimilarityMatcher()

        @Test
        fun `(위치 기반) 다른 알파벳 개수 기준 테스트 1`() {
            println("=== 위치 기반 차이 계산 테스트 1 ===")
            println("-Usage: findByPositionDifferences()")

            println("타겟: '$target'")
            println()

            // 위치 기반 매칭
            val positionResults = matcher.findByPositionDifferences(data, target, 5)
            println("위치 기반 결과:")
            positionResults.forEach { (text, diff) ->
                println("  $text: $diff 개 차이")
                when (text) {
                    "salary" -> assertEquals(diff, 0)
                    "sadary" -> assertEquals(diff, 1)
                    "sblbry" -> assertEquals(diff, 2)
                    "mysalard" -> assertEquals(diff, 3)
                }
            }
            println()

            val positionResults2 = matcher.findByPositionDifferences(target, 5)
            println("위치 기반 결과:")
            positionResults2.forEach { (text, diff) ->
                println("  $text: $diff 개 차이")
                when (text) {
                    "salary" -> assertEquals(diff, 0)
                    "sadary" -> assertEquals(diff, 1)
                    "sblbry" -> assertEquals(diff, 2)
                    "mysalard" -> assertEquals(diff, 3)
                }
            }
            println()
        }

        @Test
        fun `(위치 기반) 다른 알파벳 개수 기준 테스트 2`() {
            println("=== 위치 기반 (중간 문자열) 차이 계산 테스트 2 ===")
            println("-Usage: findMatchingStrings( ..., DifferenceMethod.POSITION_BASED )")

            println("타겟: '$target'")
            println()

            println("=== 위치 기반 (중간 문자열) 다른 알파벳 개수 기준 (최대 3개) ===")
            val comprehensiveResults = matcher.findMatchingStrings(
                data = data,
                target = target,
                maxDifferences = 3,
                includeSubstring = true,
                method = StringSimilarityMatcher.DifferenceMethod.POSITION_BASED
            )

            comprehensiveResults.forEach { result ->
                println("원본: ${result.originalString}")
                println("  매칭 부분: ${result.matchedPart}")
                println("  차이: ${result.differences}개 (${result.differenceMethod})")
                println("  타입: ${result.matchType}")
                println()
            }

            println("=== 위치 기반 (중간 문자열) 다른 알파벳 개수 기준 (최대 1개) ===")
            val substringResults = matcher.findMatchingStrings(
                data = data,
                target = target,
                maxDifferences = 1,
                includeSubstring = true,
                method = StringSimilarityMatcher.DifferenceMethod.POSITION_BASED
            )

            substringResults.forEach { result ->
                println("${result.originalString} -> ${result.matchedPart} (차이: ${result.differences}개)")
                when (result.originalString) {
                    "salary" -> assertEquals(result.differences, 0)
                    "mysalard" -> assertEquals(result.differences, 1)
                    "sadary" -> assertEquals(result.differences, 1)
                    "wowsblarywow" -> assertEquals(result.differences, 1)
                }
            }
            println()

            println("=== 위치 기반 (중간 문자열) 다른 알파벳 개수 기준 (target 이 긴 경우) ===")
            val target2 = "abcdefghijklmnopqrstuvwxyz"
            println("타겟: '$target2'")
            val substringResults2 = matcher.findMatchingStrings(
                data = data,
                target = target2,
                maxDifferences = 1,
                includeSubstring = true,
                method = StringSimilarityMatcher.DifferenceMethod.POSITION_BASED
            )

            substringResults2.forEach { result ->
                println("${result.originalString} -> ${result.matchedPart} (차이: ${result.differences}개)")
            }
            println()
        }
    }

    @Nested
    inner class DataProvider {
        val matcher = StringSimilarityMatcher()

        @Test
        fun `DataProvider 함수 테스트`() {
            println("[${matcher.dataSize()}]: ${matcher.getData()}")
            matcher.addData("asdsa")
            matcher.addData("가나다라")
            println("isDataEmpty: ${matcher.isDataEmpty()}")
            println("[${matcher.dataSize()}]: ${matcher.getData()}")
        }
    }

    @Nested
    inner class Etc {

        @Test
        fun `Enum entries 테스트`() {
            val e1 = StringSimilarityMatcher.SimilarityMethod.entries.toTypedArray()
            assertEquals(StringSimilarityMatcher.SimilarityMethod.LEVENSHTEIN, e1[0])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.JACCARD, e1[1])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.COSINE, e1[2])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.JARO, e1[3])

            val e2 = StringSimilarityMatcher.DifferenceMethod.entries.toTypedArray()
            assertEquals(StringSimilarityMatcher.DifferenceMethod.FREQUENCY_BASED, e2[0])
            assertEquals(StringSimilarityMatcher.DifferenceMethod.POSITION_BASED, e2[1])

            val e3 = StringSimilarityMatcher.MatchType.entries.toTypedArray()
            assertEquals(StringSimilarityMatcher.MatchType.EXACT_LENGTH, e3[0])
            assertEquals(StringSimilarityMatcher.MatchType.SUBSTRING, e3[1])
            assertEquals(StringSimilarityMatcher.MatchType.REVERSE_SUBSTRING, e3[2])
        }
    }
}
