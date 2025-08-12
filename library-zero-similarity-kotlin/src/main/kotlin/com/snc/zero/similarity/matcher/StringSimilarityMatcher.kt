package com.snc.zero.similarity.matcher

import com.snc.zero.similarity.data.InMemoryStringDataProvider
import com.snc.zero.similarity.data.StringDataProvider
import kotlin.math.abs
import kotlin.math.sqrt

data class SimilarityResult(
    val text: String,
    val similarity: Double,
    val method: String
)

data class SubstringMatch(
    val substring: String,
    val startIndex: Int,
    val differences: Int,
    val fullString: String
)

data class MatchResult(
    val originalString: String,
    val matchedPart: String,
    val matchType: StringSimilarityMatcher.MatchType,
    val differences: Int,
    val startIndex: Int,
    val differenceMethod: StringSimilarityMatcher.DifferenceMethod = StringSimilarityMatcher.DifferenceMethod.FREQUENCY_BASED
)

data class PositionDifferenceResult(
    val differences: Int,
    val offset: Int,
    val alignedPart1: String,
    val alignedPart2: String
)

class StringSimilarityMatcher(
    private val dataProvider: StringDataProvider = InMemoryStringDataProvider()
) {

    // 레벤슈타인 거리(Levenshtein Distance, https://en.wikipedia.org/wiki/Levenshtein_distance),
    // 자카드 유사도(Jaccard Similarity, 간단한 키워드 검색, https://en.wikipedia.org/wiki/Jaccard_index),
    // 코사인 유사도(Cosine Similarity, 단어 빈도 고려, https://en.wikipedia.org/wiki/Cosine_similarity),
    // 자로 유사도(Jaro Similarity, https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance),
    // 자로-윙클러 유사도(Jaro-Winkler Similarity, 공통 접두사에 가중치 부여, https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance)
    // 는 문자열이나 문서 간의 유사도 측정에 널리 사용되는 대표적인 거리/유사도 지표입니다.
    // https://blog.harampark.com/blog/nlp-string-distance/
    // 요약 비교표
    // | 지표		      | 입력 형태		       | 핵심 개념        | 순서 고려 | 사용 예시	                  |
    // |------------------|-------------------|----------------|---------|---------------------------|
    // | Levenshtein      | 문자열		       | 편집 거리	    |    (O)  | 오타 수정, 유사 문자열 비교   |
    // | Jaccard	      | 집합			       | 교집합/합집합	|    (X)  |	중복 탐지, 클러스터링        |
    // | Cosine		      | 벡터 (TF-IDF 등) | 방향 유사도	    |    (X)  |	문서 유사도, 검색 시스템      |
    // | Jaro		      | 문자열		       | 일치/전치	    |    (O)  |	이름 매칭, 레코드 연결       |
    // | Jaro-Winkler     | 문자열		       | 일치/전치+접두사|    (O)  |	이름 매칭, 스펠링 체크       |
    enum class SimilarityMethod {
        LEVENSHTEIN, // 편집 거리 기반
        JACCARD, // 집합 기반
        COSINE, // 벡터 기반
        JARO, // 일치 문자와 전치 기반
        JARO_WINKLER // 일치 문자와 전치 기반 + 공통 접두사 가중치
    }

    enum class DifferenceMethod {
        FREQUENCY_BASED, // 빈도 기반 (기존 방식)
        POSITION_BASED // 위치 기반 (새로운 방식)
    }

    enum class MatchType {
        EXACT_LENGTH, // 같은 길이 문자열 매칭
        SUBSTRING, // 원본 문자열에서 타겟을 부분 문자열로 찾음
        REVERSE_SUBSTRING // 타겟에서 원본을 부분 문자열로 찾음
    }

    /**
     * 데이터 제공자의 데이터를 가져옴
     */
    fun getData(): List<String> = dataProvider.getData()

    /**
     * 데이터 추가
     */
    fun addData(items: List<String>) = dataProvider.addData(items)

    /**
     * 데이터 추가 (단일 항목)
     */
    fun addData(item: String) = dataProvider.addData(item)

    /**
     * 데이터 삭제
     */
    fun clearData() = dataProvider.clearData()

    /**
     * 데이터 크기
     */
    fun dataSize(): Int = dataProvider.size()

    /**
     * 데이터가 비어있는지 확인
     */
    fun isDataEmpty(): Boolean = dataProvider.isEmpty()

    /**
     * 레벤슈타인 거리 계산
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) {
            dp[i][0] = i
        }
        for (j in 0..len2) {
            dp[0][j] = j
        }

        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1, // 삭제
                    dp[i][j - 1] + 1, // 삽입
                    dp[i - 1][j - 1] + cost // 교체
                )
            }
        }
        return dp[len1][len2]
    }

    /**
     * 레벤슈타인 거리를 이용한 유사도 계산 (0.0 ~ 1.0)
     */
    private fun levenshteinSimilarity(s1: String, s2: String): Double {
        val maxLen = maxOf(s1.length, s2.length)
        if (maxLen == 0) {
            return 1.0
        }
        val distance = levenshteinDistance(s1, s2)
        return 1.0 - (distance.toDouble() / maxLen)
    }

    /**
     * 자카드 유사도 계산 (문자 집합 기반)
     */
    private fun jaccardSimilarity(s1: String, s2: String): Double {
        val set1 = s1.toSet()
        val set2 = s2.toSet()
        val intersection = set1.intersect(set2).size
        val union = set1.union(set2).size
        return if (union == 0) {
            0.0
        } else {
            intersection.toDouble() / union
        }
    }

    /**
     * 코사인 유사도 계산 (문자 빈도 기반)
     */
    private fun cosineSimilarity(s1: String, s2: String): Double {
        val freq1 = s1.groupingBy { it }.eachCount()
        val freq2 = s2.groupingBy { it }.eachCount()
        val allChars = freq1.keys + freq2.keys

        var dotProduct = 0.0
        var norm1 = 0.0
        var norm2 = 0.0

        for (char in allChars) {
            val f1 = freq1[char] ?: 0
            val f2 = freq2[char] ?: 0
            dotProduct += f1 * f2
            norm1 += f1 * f1
            norm2 += f2 * f2
        }

        return if (norm1 == 0.0 || norm2 == 0.0) {
            0.0
        } else {
            dotProduct / (sqrt(norm1) * sqrt(norm2))
        }
    }

    /**
     * 자로 유사도 계산
     * 두 문자열 간의 일치하는 문자와 전치(transposition)를 고려한 유사도
     */
    fun jaroSimilarity(s1: String, s2: String): Double {
        if (s1 == s2) return 1.0
        if (s1.isEmpty() || s2.isEmpty()) return 0.0

        val matchWindow = (maxOf(s1.length, s2.length) / 2) - 1
        if (matchWindow < 0) return 0.0

        val (matches, s1Matches, s2Matches) = findMatches(s1, s2, matchWindow)
        if (matches == 0) return 0.0

        val transpositions = countTranspositions(s1, s2, s1Matches, s2Matches)

        return (matches.toDouble() / s1.length +
            matches.toDouble() / s2.length +
            (matches - transpositions / 2.0) / matches) / 3.0
    }

    /**
     * 일치하는 문자들을 찾아 반환한다
     */
    private fun findMatches(s1: String, s2: String, matchWindow: Int): Triple<Int, BooleanArray, BooleanArray> {
        val s1Matches = BooleanArray(s1.length)
        val s2Matches = BooleanArray(s2.length)
        var matches = 0

        for (i in s1.indices) {
            val start = maxOf(0, i - matchWindow)
            val end = minOf(i + matchWindow + 1, s2.length)

            for (j in start until end) {
                if (s2Matches[j] || s1[i] != s2[j]) continue
                s1Matches[i] = true
                s2Matches[j] = true
                matches++
                break
            }
        }

        return Triple(matches, s1Matches, s2Matches)
    }

    /**
     * 전치(transposition) 개수를 계산한다
     */
    private fun countTranspositions(
        s1: String,
        s2: String,
        s1Matches: BooleanArray,
        s2Matches: BooleanArray
    ): Int {
        var transpositions = 0
        var k = 0

        for (i in s1.indices) {
            if (!s1Matches[i]) continue
            while (!s2Matches[k]) k++
            if (s1[i] != s2[k]) transpositions++
            k++
        }

        return transpositions
    }

    /**
     * 자로-윙클러 유사도 계산
     * 자로 유사도를 기반으로 공통 접두사에 가중치를 부여하여 계산
     * @param s1 첫 번째 문자열
     * @param s2 두 번째 문자열
     * @param threshold Jaro 유사도 임계값 (기본 0.7, 이 값 이상일 때만 접두사 보너스 적용)
     * @param scalingFactor 접두사 가중치 계수 (기본 0.1, 최대 0.25까지 권장)
     */
    fun jaroWinklerSimilarity(
        s1: String,
        s2: String,
        threshold: Double = 0.7,
        scalingFactor: Double = 0.1
    ): Double {
        val jaroScore = jaroSimilarity(s1, s2)

        // Jaro 점수가 임계값 미만이면 그대로 반환
        if (jaroScore < threshold) {
            return jaroScore
        }

        // 공통 접두사 길이 계산 (최대 4자까지)
        val commonPrefixLength = minOf(
            s1.zip(s2).takeWhile { (c1, c2) -> c1 == c2 }.size,
            4
        )

        // Jaro-Winkler 점수 계산
        return jaroScore + (commonPrefixLength * scalingFactor * (1 - jaroScore))
    }

    /**
     * 다른 알파벳 개수 계산 (빈도 기반)
     */
    private fun differentCharCount(s1: String, s2: String): Int {
        val freq1 = s1.groupingBy { it }.eachCount()
        val freq2 = s2.groupingBy { it }.eachCount()
        val allChars = freq1.keys + freq2.keys

        return allChars.sumOf { char ->
            abs((freq1[char] ?: 0) - (freq2[char] ?: 0))
        }
    }

    /**
     * 두 같은 길이 문자열의 다른 문자 개수 계산
     */
    private fun countDifferences(s1: String, s2: String): Int {
        if (s1.length != s2.length) {
            return Int.MAX_VALUE
        }
        return s1.zip(s2).count { (c1, c2) -> c1 != c2 }
    }

    /**
     * 최적 정렬을 통한 위치 기반 차이 계산
     * 문자열의 모든 가능한 정렬에서 최소 차이를 찾음
     */
    private fun optimalPositionBasedDifferences(s1: String, s2: String): PositionDifferenceResult {
        if (s1.length == s2.length) {
            val diff = countDifferences(s1, s2)
            return PositionDifferenceResult(diff, 0, s1, s2)
        }

        val shorter = if (s1.length < s2.length) s1 else s2
        val longer = if (s1.length < s2.length) s2 else s1
        val isS1Shorter = s1.length < s2.length

        var minDifferences = Int.MAX_VALUE
        var bestOffset = 0
        var bestShorterPart = shorter
        var bestLongerPart = longer

        // 짧은 문자열을 긴 문자열의 모든 위치에서 비교
        for (offset in 0..longer.length - shorter.length) {
            val longerSubstring = longer.substring(offset, offset + shorter.length)
            val positionDiff = countDifferences(shorter, longerSubstring)
            val totalDiff = positionDiff + (longer.length - shorter.length)

            if (totalDiff < minDifferences) {
                minDifferences = totalDiff
                bestOffset = offset
                bestShorterPart = shorter
                bestLongerPart = longerSubstring
            }
        }

        return if (isS1Shorter) {
            PositionDifferenceResult(minDifferences, bestOffset, bestShorterPart, bestLongerPart)
        } else {
            PositionDifferenceResult(minDifferences, bestOffset, bestLongerPart, bestShorterPart)
        }
    }

    /**
     * 유사도를 이용한 문자열 매칭 (내부 데이터 사용)
     * @param target 타겟 문자열
     * @param minSimilarity 최소 유사도 임계값 (0.0 ~ 1.0)
     * @param method 사용할 유사도 계산 방법
     */
    fun findSimilarStrings(
        target: String,
        minSimilarity: Double = 0.5,
        method: SimilarityMethod = SimilarityMethod.LEVENSHTEIN
    ): List<SimilarityResult> {
        return findSimilarStrings(dataProvider.getData(), target, minSimilarity, method)
    }

    /**
     * 유사도를 이용한 문자열 매칭 (외부 데이터 사용)
     * @param data 검색할 문자열 리스트
     * @param target 타겟 문자열
     * @param minSimilarity 최소 유사도 임계값 (0.0 ~ 1.0)
     * @param method 사용할 유사도 계산 방법
     */
    fun findSimilarStrings(
        data: List<String>,
        target: String,
        minSimilarity: Double = 0.5,
        method: SimilarityMethod = SimilarityMethod.LEVENSHTEIN
    ): List<SimilarityResult> {
        return data.mapNotNull { text ->
            val similarity = when (method) {
                SimilarityMethod.LEVENSHTEIN -> levenshteinSimilarity(target, text)
                SimilarityMethod.JACCARD -> jaccardSimilarity(target, text)
                SimilarityMethod.COSINE -> cosineSimilarity(target, text)
                SimilarityMethod.JARO -> jaroSimilarity(target, text)
                SimilarityMethod.JARO_WINKLER -> jaroWinklerSimilarity(target, text)
            }

            if (similarity >= minSimilarity) {
                SimilarityResult(text, similarity, method.name)
            } else { null }
        }.sortedByDescending { it.similarity }
    }

    /**
     * 다른 알파벳 개수를 이용한 문자열 매칭 (내부 데이터 사용)
     * @param target 타겟 문자열
     * @param maxDifferentChars 최대 다른 알파벳 개수
     */
    fun findByDifferentCharCount(
        target: String,
        maxDifferentChars: Int = 3
    ): List<Pair<String, Int>> {
        return findByDifferentCharCount(dataProvider.getData(), target, maxDifferentChars)
    }

    /**
     * 다른 알파벳 개수를 이용한 문자열 매칭 (외부 데이터 사용)
     * @param data 검색할 문자열 리스트
     * @param target 타겟 문자열
     * @param maxDifferentChars 최대 다른 알파벳 개수
     */
    fun findByDifferentCharCount(
        data: List<String>,
        target: String,
        maxDifferentChars: Int = 3
    ): List<Pair<String, Int>> {
        return data.mapNotNull { text ->
            val diffCount = differentCharCount(target, text)
            if (diffCount <= maxDifferentChars) {
                text to diffCount
            } else { null }
        }.sortedBy { it.second }
    }

    /**
     * 위치 기반 차이를 이용한 문자열 매칭 (내부 데이터 사용)
     * @param target 타겟 문자열
     * @param maxDifferences 최대 허용 차이 개수
     */
    fun findByPositionDifferences(
        target: String,
        maxDifferences: Int = 3
    ): List<Pair<String, Int>> {
        return findByPositionDifferences(dataProvider.getData(), target, maxDifferences)
    }

    /**
     * 위치 기반 차이를 이용한 문자열 매칭 (외부 데이터 사용)
     * @param data 검색할 문자열 리스트
     * @param target 타겟 문자열
     * @param maxDifferences 최대 허용 차이 개수
     */
    fun findByPositionDifferences(
        data: List<String>,
        target: String,
        maxDifferences: Int = 3
    ): List<Pair<String, Int>> {
        return data.mapNotNull { text ->
            val result = optimalPositionBasedDifferences(target, text)
            if (result.differences <= maxDifferences) {
                text to result.differences
            } else { null }
        }.sortedBy { it.second }
    }

    /**
     * 부분 문자열 포함 검사 (슬라이딩 윈도우 방식)
     * @param source 원본 문자열
     * @param target 찾을 부분 문자열
     * @param maxDifferences 허용할 최대 차이 문자 개수
     * @param method 차이 계산 방법
     */
    private fun findSubstringWithDifferences(
        source: String,
        target: String,
        maxDifferences: Int,
        method: DifferenceMethod = DifferenceMethod.POSITION_BASED
    ): SubstringMatch? {
        if (target.length > source.length) return null

        for (i in 0..source.length - target.length) {
            val substring = source.substring(i, i + target.length)
            val differences = when (method) {
                DifferenceMethod.POSITION_BASED -> countDifferences(substring, target)
                DifferenceMethod.FREQUENCY_BASED -> differentCharCount(substring, target)
            }

            if (differences <= maxDifferences) {
                return SubstringMatch(
                    substring = substring,
                    startIndex = i,
                    differences = differences,
                    fullString = source
                )
            }
        }
        return null
    }

    /**
     * 포함 문자열 검사를 포함한 종합 매칭 (내부 데이터 사용)
     * @param target 타겟 문자열
     * @param maxDifferences 허용할 최대 차이 문자 개수
     * @param includeSubstring 부분 문자열 포함 검사 여부
     * @param method 차이 계산 방법 (위치 기반 또는 빈도 기반)
     */
    fun findMatchingStrings(
        target: String,
        maxDifferences: Int = 1,
        includeSubstring: Boolean = true,
        method: DifferenceMethod = DifferenceMethod.POSITION_BASED
    ): List<MatchResult> {
        return findMatchingStrings(dataProvider.getData(), target, maxDifferences, includeSubstring, method)
    }

    /**
     * 포함 문자열 검사를 포함한 종합 매칭 (외부 데이터 사용)
     * @param data 검색할 문자열 리스트
     * @param target 타겟 문자열
     * @param maxDifferences 허용할 최대 차이 문자 개수
     * @param includeSubstring 부분 문자열 포함 검사 여부
     * @param method 차이 계산 방법 (위치 기반 또는 빈도 기반)
     */
    fun findMatchingStrings(
        data: List<String>,
        target: String,
        maxDifferences: Int = 1,
        includeSubstring: Boolean = true,
        method: DifferenceMethod = DifferenceMethod.POSITION_BASED
    ): List<MatchResult> {
        val results = mutableListOf<MatchResult>()

        for (text in data) {
            val matchResult = findBestMatch(text, target, maxDifferences, includeSubstring, method)
            if (matchResult != null) {
                results.add(matchResult)
            }
        }

        return results.sortedBy { it.differences }
    }

    private fun findBestMatch(
        text: String,
        target: String,
        maxDifferences: Int,
        includeSubstring: Boolean,
        method: DifferenceMethod
    ): MatchResult? {
        // 1. 완전 문자열 비교 (같은 길이)
        if (text.length == target.length) {
            return checkExactLengthMatch(text, target, maxDifferences, method)
        }

        // 2. 다른 길이 문자열 비교 (위치 기반만 지원)
        if (method == DifferenceMethod.POSITION_BASED) {
            val positionMatch = checkPositionBasedMatch(text, target, maxDifferences, method)
            if (positionMatch != null) return positionMatch
        }

        // 3. 부분 문자열 검사
        if (includeSubstring) {
            return checkSubstringMatches(text, target, maxDifferences, method)
        }

        return null
    }

    private fun checkExactLengthMatch(
        text: String,
        target: String,
        maxDifferences: Int,
        method: DifferenceMethod
    ): MatchResult? {
        val differences = when (method) {
            DifferenceMethod.POSITION_BASED -> countDifferences(text, target)
            DifferenceMethod.FREQUENCY_BASED -> differentCharCount(text, target)
        }

        return if (differences <= maxDifferences) {
            MatchResult(
                originalString = text,
                matchedPart = text,
                matchType = MatchType.EXACT_LENGTH,
                differences = differences,
                startIndex = 0,
                differenceMethod = method
            )
        } else {
            null
        }
    }

    private fun checkPositionBasedMatch(
        text: String,
        target: String,
        maxDifferences: Int,
        method: DifferenceMethod
    ): MatchResult? {
        val result = optimalPositionBasedDifferences(text, target)

        return if (result.differences <= maxDifferences) {
            MatchResult(
                originalString = text,
                matchedPart = if (text.length > target.length) result.alignedPart1 else text,
                matchType = MatchType.EXACT_LENGTH,
                differences = result.differences,
                startIndex = result.offset,
                differenceMethod = method
            )
        } else {
            null
        }
    }

    private fun checkSubstringMatches(
        text: String,
        target: String,
        maxDifferences: Int,
        method: DifferenceMethod
    ): MatchResult? {
        // 타겟이 더 짧은 경우 (타겟이 텍스트의 부분 문자열)
        if (target.length < text.length) {
            return checkTargetAsSubstring(text, target, maxDifferences, method)
        }

        // 타겟이 더 긴 경우 (텍스트가 타겟의 부분 문자열)
        if (target.length > text.length) {
            return checkTextAsSubstring(text, target, maxDifferences, method)
        }

        return null
    }

    private fun checkTargetAsSubstring(
        text: String,
        target: String,
        maxDifferences: Int,
        method: DifferenceMethod
    ): MatchResult? {
        val substringMatch = findSubstringWithDifferences(text, target, maxDifferences, method)

        return if (substringMatch != null) {
            MatchResult(
                originalString = text,
                matchedPart = substringMatch.substring,
                matchType = MatchType.SUBSTRING,
                differences = substringMatch.differences,
                startIndex = substringMatch.startIndex,
                differenceMethod = method
            )
        } else {
            null
        }
    }

    private fun checkTextAsSubstring(
        text: String,
        target: String,
        maxDifferences: Int,
        method: DifferenceMethod
    ): MatchResult? {
        val substringMatch = findSubstringWithDifferences(target, text, maxDifferences, method)

        return if (substringMatch != null) {
            MatchResult(
                originalString = text,
                matchedPart = text,
                matchType = MatchType.REVERSE_SUBSTRING,
                differences = substringMatch.differences,
                startIndex = substringMatch.startIndex,
                differenceMethod = method
            )
        } else {
            null
        }
    }
}
