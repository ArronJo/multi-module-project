package com.snc.zero.similarity.matcher

import com.snc.zero.hangul.engtokor.EngToKor
import com.snc.zero.similarity.data.InMemoryStringDataProvider
import com.snc.zero.similarity.data.StringDataProvider
import kotlin.math.abs
import kotlin.math.max
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

// 문자열 포함 여부 검사 결과
data class ContainmentResult(
    val text: String,
    val similarity: Int, // 0~100 점수
    val matchType: ContainmentMatchType,
    val method: String = "CONTAINMENT"
)

enum class ContainmentMatchType {
    EXACT_MATCH, // 정확히 일치
    CONTAINS_SEARCH, // 키워드가 검색어를 포함
    CONTAINED_IN_SEARCH, // 키워드가 검색어에 포함됨
    COMMON_CHARS // 공통 문자 기반 유사도
}

class StringSimilarityMatcher(
    private val dataProvider: StringDataProvider = InMemoryStringDataProvider()
) {

    // 레벤슈타인 거리(Levenshtein Distance, https://en.wikipedia.org/wiki/Levenshtein_distance),
    // 다메라우-레벤슈타인 거리(Damerau–Levenshtein distance, https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance),
    // 자카드 유사도(Jaccard Similarity, 간단한 키워드 검색, https://en.wikipedia.org/wiki/Jaccard_index),
    // 코사인 유사도(Cosine Similarity, 단어 빈도 고려, https://en.wikipedia.org/wiki/Cosine_similarity),
    // 자로 유사도(Jaro Similarity, https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance),
    // 자로-윙클러 유사도(Jaro-Winkler Similarity, 공통 접두사에 가중치 부여, https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance)
    // 는 문자열이나 문서 간의 유사도 측정에 널리 사용되는 대표적인 거리/유사도 지표입니다.
    // https://blog.harampark.com/blog/nlp-string-distance/
    // 요약 비교표
    // | 지표		         | 입력 형태		     | 핵심 개념        | 순서 고려 | 사용 예시	                  |
    // |---------------------|-------------------|----------------|---------|---------------------------|
    // | Levenshtein         | 문자열		         | 편집 거리	      |    (O)  | 오타 수정, 유사 문자열 비교	|
    // | Damerau-Levenshtein | 문자열		         | 편집 거리	      |    (O)  | Levenshtein + 인접 문자 전환(Transposition) 연산까지 허용	|
    // | Jaccard	         | 집합			     | 교집합/합집합     |    (X)  | 중복 탐지, 클러스터링	|
    // | Cosine		         | 벡터 (TF-IDF 등)    | 방향 유사도	  |    (X)  | 문서 유사도, 검색 시스템	|
    // | Jaro		         | 문자열		         | 일치/전치	      |    (O)  | 이름 매칭, 레코드 연결	|
    // | Jaro-Winkler        | 문자열		         | 일치/전치+접두사   |    (O)  | Jaro + 공통 접두사 보너스를 추가함	|
    // | Containment         | 문자열              | 포함 관계        |    (O)  | 검색, 자동완성, 키워드 매칭  |
    enum class SimilarityMethod {
        LEVENSHTEIN, // 편집 거리 기반
        DAMERAU_LEVENSHTEIN, // 편집 거리 기반
        JACCARD, // 집합 기반
        COSINE, // 벡터 기반
        JARO, // 일치 문자와 전치 기반
        JARO_WINKLER, // 일치 문자와 전치 기반 + 공통 접두사 가중치
        CONTAINMENT // 문자열 포함 여부 기반 (새로 추가)
    }

    enum class DifferenceMethod {
        FREQUENCY_BASED, // 빈도 기반
        POSITION_BASED // 위치 기반
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
     * 영문인지 확인하는 함수
     */
    private fun isEnglish(text: String): Boolean {
        return text.matches(Regex("^[a-zA-Z\\s]+$"))
    }

    /**
     * 한글 → 영문 키보드 변환 (간단한 매핑 예시)
     */
    private fun korToEng(text: String): String {
        val korToEngMap = mapOf(
            'ㅂ' to 'q', 'ㅈ' to 'w', 'ㄷ' to 'e', 'ㄱ' to 'r', 'ㅅ' to 't',
            'ㅛ' to 'y', 'ㅕ' to 'u', 'ㅑ' to 'i', 'ㅐ' to 'o', 'ㅔ' to 'p',
            'ㅁ' to 'a', 'ㄴ' to 's', 'ㅇ' to 'd', 'ㄹ' to 'f', 'ㅎ' to 'g',
            'ㅗ' to 'h', 'ㅓ' to 'j', 'ㅏ' to 'k', 'ㅣ' to 'l',
            'ㅋ' to 'z', 'ㅌ' to 'x', 'ㅊ' to 'c', 'ㅍ' to 'v', 'ㅠ' to 'b',
            'ㅜ' to 'n', 'ㅡ' to 'm'
        )

        return text.map { char ->
            korToEngMap[char] ?: char
        }.joinToString("")
    }

    /**
     * 변환 가능한 모든 형태를 반환
     */
    private fun getConvertedTerms(searchTerm: String): List<String> {
        val terms = mutableSetOf<String>()
        val lowerTerm = searchTerm.lowercase()

        // 원본
        terms.add(lowerTerm)

        // 영문 → 한글
        if (isEnglish(searchTerm)) {
            val converted = EngToKor.engToKor(searchTerm).lowercase()
            if (converted.isNotEmpty()) terms.add(converted)
        }

        // 한글 → 영문
        val engConverted = korToEng(searchTerm)
        if (engConverted.isNotEmpty()) terms.add(engConverted.lowercase())

        return terms.toList()
    }

    /**
     * 문자열 포함 여부 검사 (가장 기본적인 방법)
     * - searchTerm: 검색어
     * - keyword: 대상 키워드
     */
    private fun calculateContainmentSimilarity(keyword: String, searchTerm: String): ContainmentResult {
        val keywordLower = keyword.lowercase()
        val searchLower = searchTerm.lowercase()

        // 정확히 일치하는 경우
        if (keywordLower == searchLower) {
            return ContainmentResult(keyword, 100, ContainmentMatchType.EXACT_MATCH)
        }

        // 검색어가 키워드에 포함된 경우
        if (keywordLower.contains(searchLower)) {
            return ContainmentResult(keyword, 90, ContainmentMatchType.CONTAINS_SEARCH)
        }

        // 키워드가 검색어에 포함된 경우 (검색어가 더 긴 경우)
        if (searchLower.contains(keywordLower)) {
            return ContainmentResult(keyword, 85, ContainmentMatchType.CONTAINED_IN_SEARCH)
        }

        // 공통 문자 개수로 유사도 계산
        var commonChars = 0
        val searchChars = searchLower.toList()
        val keywordChars = keywordLower.toList()

        for (char in searchChars) {
            if (keywordChars.contains(char)) {
                commonChars++
            }
        }

        val similarity = ((commonChars.toDouble() / max(searchLower.length, keywordLower.length)) * 100).toInt()
        return ContainmentResult(keyword, similarity, ContainmentMatchType.COMMON_CHARS)
    }

    /**
     * 종합 문자열 포함 여부 유사도 계산
     */
    private fun getOverallContainmentSimilarity(keyword: String, searchTerm: String): ContainmentResult {
        val convertedTerms = getConvertedTerms(searchTerm)
        var bestResult = ContainmentResult(keyword, 0, ContainmentMatchType.COMMON_CHARS)

        for (term in convertedTerms) {
            val result = calculateContainmentSimilarity(keyword, term)
            if (result.similarity > bestResult.similarity) {
                bestResult = result
            }
        }

        return bestResult
    }

    /**
     * 문자열 포함 여부를 이용한 문자열 매칭 (내부 데이터 사용)
     * @param target 타겟 문자열
     * @param minSimilarity 최소 유사도 임계값 (0~100)
     */
    fun findByContainment(
        target: String,
        minSimilarity: Int = 50
    ): List<ContainmentResult> {
        return findByContainment(dataProvider.getData(), target, minSimilarity)
    }

    /**
     * 문자열 포함 여부를 이용한 문자열 매칭 (외부 데이터 사용)
     * @param data 검색할 문자열 리스트
     * @param target 타겟 문자열
     * @param minSimilarity 최소 유사도 임계값 (0~100)
     */
    fun findByContainment(
        data: List<String>,
        target: String,
        minSimilarity: Int = 50
    ): List<ContainmentResult> {
        return data.mapNotNull { text ->
            val result = getOverallContainmentSimilarity(text, target)
            if (result.similarity >= minSimilarity) {
                result
            } else {
                null
            }
        }.sortedByDescending { it.similarity }
    }

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
     * 다메라우-레벤슈타인 거리 계산
     * Damerau–Levenshtein Distance는 여기에 인접 문자 전환(Transposition) 연산까지 허용하는 버전
     */
    private fun damerauLevenshteinDistance(s1: String, s2: String): Int {
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

                // Damerau-Levenshtein: 인접 문자 전환 체크
                if (i > 1 && j > 1 &&
                    s1[i - 1] == s2[j - 2] &&
                    s1[i - 2] == s2[j - 1]
                ) {
                    dp[i][j] = minOf(
                        dp[i][j],
                        dp[i - 2][j - 2] + 1 // 전환
                    )
                }
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

    private fun damerauLevenshteinSimilarity(s1: String, s2: String): Double {
        val maxLen = maxOf(s1.length, s2.length)
        if (maxLen == 0) {
            return 1.0
        }
        val distance = damerauLevenshteinDistance(s1, s2)
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
     * @param minSimilarity 최소 유사도 임계값 (0.0 ~ 1.0, CONTAINMENT의 경우 0~100)
     * @param method 사용할 유사도 계산 방법
     */
    fun findSimilarStrings(
        data: List<String>,
        target: String,
        minSimilarity: Double = 0.5,
        method: SimilarityMethod = SimilarityMethod.LEVENSHTEIN
    ): List<SimilarityResult> {
        return when (method) {
            SimilarityMethod.CONTAINMENT -> {
                // CONTAINMENT 방법은 0~100 점수를 사용하므로 별도 처리
                val containmentResults = findByContainment(data, target, (minSimilarity * 100).toInt())
                containmentResults.map { result ->
                    SimilarityResult(result.text, result.similarity / 100.0, result.method)
                }
            }
            else -> {
                data.mapNotNull { text ->
                    val similarity = when (method) {
                        SimilarityMethod.LEVENSHTEIN -> levenshteinSimilarity(target, text)
                        SimilarityMethod.DAMERAU_LEVENSHTEIN -> damerauLevenshteinSimilarity(target, text)
                        SimilarityMethod.JACCARD -> jaccardSimilarity(target, text)
                        SimilarityMethod.COSINE -> cosineSimilarity(target, text)
                        SimilarityMethod.JARO -> jaroSimilarity(target, text)
                        SimilarityMethod.JARO_WINKLER -> jaroWinklerSimilarity(target, text)
                        else -> 0.0 // CONTAINMENT는 위에서 처리됨
                    }

                    if (similarity >= minSimilarity) {
                        SimilarityResult(text, similarity, method.name)
                    } else {
                        null
                    }
                }.sortedByDescending { it.similarity }
            }
        }
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
            } else {
                null
            }
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
            } else {
                null
            }
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
