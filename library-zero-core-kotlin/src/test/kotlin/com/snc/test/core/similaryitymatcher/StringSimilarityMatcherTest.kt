package com.snc.test.core.similaryitymatcher

import com.snc.zero.core.similaritymatcher.StringSimilarityMatcher
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

@Suppress("NonAsciiCharacters")
class StringSimilarityMatcherTest : BaseJUnit5Test() {

    private var data: List<String> = listOf(
        "salary",
        "mysalard",
        "mysalaygood",
        "sadary",
        "payback",
        "sblbry",
        "wowsblarywow"
    )

    private val matcher = StringSimilarityMatcher()

    @Test
    fun `SimilarityMatcher 유사도 테스트 1`() {
        println("=== 유사도 테스트 1 ===")

        val target = "salary"
        println("target: $target")
        println()

        println("=== 레벤슈타인 유사도 (최소 0.3) ===")
        println("-Usage: findSimilarStrings( ..., SimilarityMethod.LEVENSHTEIN )")
        val levenshteinResults = matcher.findSimilarStrings(
            data,
            target,
            0.3,
            StringSimilarityMatcher.SimilarityMethod.LEVENSHTEIN
        )
        levenshteinResults.forEach { result ->
            println("${result.text}: ${String.format("%.3f", result.similarity)}")
        }
        println()

        println("=== 자카드 유사도 (최소 0.3) ===")
        println("-Usage: findSimilarStrings( ..., SimilarityMethod.JACCARD )")
        val jaccardResults = matcher.findSimilarStrings(
            data,
            target,
            0.3,
            StringSimilarityMatcher.SimilarityMethod.JACCARD
        )
        jaccardResults.forEach { result ->
            println("${result.text}: ${String.format("%.3f", result.similarity)}")
        }
        println()

        println("=== 코사인 유사도 (최소 0.3) ===")
        println("-Usage: findSimilarStrings( ..., SimilarityMethod.COSINE )")
        val cosineResults = matcher.findSimilarStrings(
            data,
            target,
            0.3,
            StringSimilarityMatcher.SimilarityMethod.COSINE
        )
        cosineResults.forEach { result ->
            println("${result.text}: ${String.format("%.3f", result.similarity)}")
        }
        println()
    }

    @Test
    fun `SimilarityMatcher (빈도 기반) 다른 알파벳 개수 기준 테스트 1`() {
        println("=== 빈도 기반 차이 계산 테스트 1 ===")
        println("-Usage: findByDifferentCharCount()")

        val target = "salary"
        println("타겟: '$target'")
        println()

        println("=== (빈도 기반) 다른 알파벳 개수 기준 (최대 4개) ===")
        val frequencyResults1 = matcher.findByDifferentCharCount(data, target, 4)
        frequencyResults1.forEach { (text, diffCount) ->
            println("  $text: $diffCount 개 차이")
        }
        println()

        println("=== (빈도 기반) 다른 알파벳 개수 기준 (최대 1개) ===")
        val frequencyResults2 = matcher.findByDifferentCharCount(data, target, 1)
        frequencyResults2.forEach { (text, diffCount) ->
            println("$text: 다른 문자 $diffCount 개")
        }
        println()
    }

    @Test
    fun `SimilarityMatcher (빈도 기반) 다른 알파벳 개수 기준 테스트 2`() {
        println("=== 빈도 기반 차이 계산 테스트 2 ===")
        println("-Usage: findMatchingStrings( ..., DifferenceMethod.FREQUENCY_BASED )")

        val target = "salary"
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
        }
        println()
    }

    @Test
    fun `SimilarityMatcher (위치 기반) 다른 알파벳 개수 기준 테스트 1`() {
        println("=== 위치 기반 차이 계산 테스트 1 ===")
        println("-Usage: findByPositionDifferences()")

        val target = "salary"
        println("타겟: '$target'")
        println()

        // 위치 기반 매칭
        val positionResults = matcher.findByPositionDifferences(data, target, 5)
        println("위치 기반 결과:")
        positionResults.forEach { (text, diff) ->
            println("  $text: $diff 개 차이")
        }
        println()
    }

    @Test
    fun `SimilarityMatcher (위치 기반) 다른 알파벳 개수 기준 테스트 2`() {
        println("=== 위치 기반 (중간 문자열) 차이 계산 테스트 2 ===")
        println("-Usage: findMatchingStrings( ..., DifferenceMethod.POSITION_BASED )")

        val target = "salary"
        println("타겟: '$target'")
        println()

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

        val substringResults = matcher.findMatchingStrings(
            data = data,
            target = target,
            maxDifferences = 1,
            includeSubstring = true,
            method = StringSimilarityMatcher.DifferenceMethod.POSITION_BASED
        )

        substringResults.forEach { result ->
            println("${result.originalString} -> ${result.matchedPart} (차이: ${result.differences}개)")
        }
        println()
    }
}
