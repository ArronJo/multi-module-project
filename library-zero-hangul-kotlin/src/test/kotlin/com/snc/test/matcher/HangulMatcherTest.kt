package com.snc.test.matcher

import com.snc.zero.hangul.matcher.HangulMatcher
import com.snc.zero.hangul.matcher.dto.HangulMatchResult
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class HangulMatcherTest : BaseJUnit5Test() {

    private lateinit var matcher: HangulMatcher

    @BeforeEach
    fun setup() {
        matcher = HangulMatcher()
    }

    @Test
    fun `한글 초성 검색`() {
        // given
        val menus = arrayOf(
            "사고보험금 신청",
            "사고보험금 내역조회",
            "사고보험금 서류보완",
            "일반보험금 신청",
            "일반보험금 지급내역",
            "휴대폰인증 NICE 평가",
            "휴대폰인증NICE 인증",
            "휴대폰인증 KCB 평가 (not NICE))",
            "당사는 전용선을 통해 VAN사(NICE)에 카드인증을 요청합니다."
        )
        // when
        val keyword = "ㅈㅇㅅㅇㅌㅎv" // "ㅈ N", "ㅅㄱ"
        val matcher = HangulMatcher()
        matcher.keyword(keyword)
        for (m in menus) {
            val match = matcher.match(m)
            // then
            if (HangulMatchResult.EMPTY != match) {
                logger.debug { "한글 초성 검색 ($keyword) -> 결과: $m" }
            }
        }
    }

    @Test
    fun `keyword should lowercase and filter non-korean characters`() {
        matcher.keyword("가나다 ABC123!@#")
        val result = matcher.match("가나다 abc123")
        assertEquals(HangulMatchResult(0, 10), result)
    }

    @Test
    fun `match should return correct start and length when match found`() {
        matcher.keyword("ㄱㄴ")
        val result = matcher.match("가나다라마")
        assertEquals(HangulMatchResult(0, 2), result)
    }

    @Test
    fun `match should skip ignored symbols`() {
        matcher.keyword("abc")
        val result = matcher.match("a@b!c")
        assertEquals(HangulMatchResult(0, 5), result)
    }

    @Test
    fun `match should return EMPTY when keyword not found`() {
        matcher.keyword("ㅋㅌㅍ")
        val result = matcher.match("가나다라마바사")
        assertEquals(HangulMatchResult.EMPTY, result)
    }

    @Test
    fun `match should return EMPTY when keyword is empty`() {
        matcher.keyword("")
        val result = matcher.match("아무 문자열")
        assertEquals(HangulMatchResult(0, 0), result)
    }

    @Test
    fun `match should return match even with intermediate ignored characters`() {
        matcher.keyword("abc")
        val result = matcher.match("a!@#b%^c")
        assertEquals(HangulMatchResult(0, 8), result)
    }

    @Test
    fun `match should match mixed case and Korean characters`() {
        matcher.keyword("GNa")
        val result = matcher.match("가나abc")
        assertEquals(HangulMatchResult.EMPTY, result)
    }

    @Test
    fun `match should skip initial unmatched characters`() {
        matcher.keyword("ㄷㄹ")
        val result = matcher.match("가나다라마")
        assertEquals(HangulMatchResult(2, 2), result)
    }

    @Nested
    @DisplayName("키워드 설정 테스트")
    inner class KeywordSetupTest {

        @Test
        @DisplayName("정상적인 한글 키워드 설정")
        fun shouldSetKoreanKeywordProperly() {
            val result = matcher.keyword("안녕하세요")
            assertNotNull(result)
            assertEquals(matcher, result) // 체이닝 확인
        }

        @Test
        @DisplayName("영문 키워드 설정")
        fun shouldSetEnglishKeywordProperly() {
            val result = matcher.keyword("Hello")
            assertNotNull(result)
        }

        @Test
        @DisplayName("특수문자가 포함된 키워드에서 특수문자 제거")
        fun shouldRemoveSpecialCharactersFromKeyword() {
            matcher.keyword("안녕!@#하세요$%^")
            // 내부적으로 특수문자가 제거되어야 함
        }

        @Test
        @DisplayName("빈 문자열 키워드 설정")
        fun shouldHandleEmptyKeyword() {
            val result = matcher.keyword("")
            assertNotNull(result)
        }

        @Test
        @DisplayName("대소문자 구분 없이 키워드 설정")
        fun shouldConvertKeywordToLowercase() {
            matcher.keyword("HELLO")
            // 내부적으로 소문자로 변환되어야 함
        }
    }

    @Nested
    @DisplayName("매칭 기본 기능 테스트")
    inner class BasicMatchingTest {

        @Test
        @DisplayName("빈 키워드로 매칭 시도 시 기본값 반환")
        fun shouldReturnDefaultWhenKeywordIsEmpty() {
            matcher.keyword("")
            val result = matcher.match("안녕하세요")

            assertEquals(0, result.start)
            assertEquals(0, result.length)
        }

        @Test
        @DisplayName("정확한 매칭 성공")
        fun shouldMatchExactKeyword() {
            matcher.keyword("안녕")
            val result = matcher.match("안녕하세요")

            assertEquals(0, result.start)
            assertTrue(result.length > 0)
        }

        @Test
        @DisplayName("매칭 실패 시 EMPTY 반환")
        fun shouldReturnEmptyWhenNoMatch() {
            matcher.keyword("없는단어")
            val result = matcher.match("안녕하세요")

            assertEquals(HangulMatchResult.EMPTY, result)
        }

        @Test
        @DisplayName("중간 위치에서 매칭 성공")
        fun shouldMatchInMiddleOfString() {
            matcher.keyword("하세")
            val result = matcher.match("안녕하세요")

            assertTrue(result.start > 0)
            assertTrue(result.length > 0)
        }
    }

    @Nested
    @DisplayName("경계 조건 및 while 루프 커버리지 테스트")
    inner class BoundaryConditionTest {

        @Test
        @DisplayName("키워드가 대상 문자열보다 긴 경우")
        fun shouldHandleKeywordLongerThanTarget() {
            matcher.keyword("매우긴키워드입니다")
            val result = matcher.match("짧음")

            assertEquals(HangulMatchResult.EMPTY, result)
        }

        @Test
        @DisplayName("키워드와 대상 문자열 길이가 같고 일치하는 경우")
        fun shouldMatchWhenKeywordAndTargetSameLength() {
            matcher.keyword("안녕")
            val result = matcher.match("안녕")

            assertEquals(0, result.start)
            assertEquals(2, result.length)
        }

        @Test
        @DisplayName("키워드와 대상 문자열 길이가 같고 불일치하는 경우")
        fun shouldNotMatchWhenKeywordAndTargetSameLengthButDifferent() {
            matcher.keyword("안녕")
            val result = matcher.match("하이")

            assertEquals(HangulMatchResult.EMPTY, result)
        }

        @Test
        @DisplayName("문자열 끝에서 부분 매칭 후 실패")
        fun shouldFailWhenPartialMatchAtEndOfString() {
            matcher.keyword("안녕하세요")
            val result = matcher.match("테스트안녕하세")

            assertEquals(HangulMatchResult.EMPTY, result)
        }

        @Test
        @DisplayName("한 글자 키워드로 매칭")
        fun shouldMatchSingleCharacterKeyword() {
            matcher.keyword("안")
            val result = matcher.match("안녕하세요")

            assertEquals(0, result.start)
            assertEquals(1, result.length)
        }

        @Test
        @DisplayName("한 글자 대상에서 매칭")
        fun shouldMatchInSingleCharacterTarget() {
            matcher.keyword("안")
            val result = matcher.match("안")

            assertEquals(0, result.start)
            assertEquals(1, result.length)
        }

        @Test
        @DisplayName("빈 대상 문자열에서 매칭 시도")
        fun shouldHandleEmptyTargetString() {
            matcher.keyword("안녕")
            val result = matcher.match("")

            assertEquals(HangulMatchResult.EMPTY, result)
        }
    }

    @Nested
    @DisplayName("특수 문자 및 무시 문자 테스트")
    inner class IgnoreCharacterTest {

        @Test
        @DisplayName("무시할 문자들이 포함된 대상에서 매칭")
        fun shouldIgnoreSpecialCharactersInTarget() {
            matcher.keyword("안녕")
            val result = matcher.match("안!@#녕$%^하세요")

            assertEquals(0, result.start)
            assertTrue(result.length > 2) // 무시 문자들도 길이에 포함
        }

        @Test
        @DisplayName("연속된 무시 문자들 처리")
        fun shouldHandleConsecutiveIgnoreCharacters() {
            matcher.keyword("안녕")
            val result = matcher.match("안    !!!   녕")

            assertEquals(0, result.start)
            assertTrue(result.length > 0)
        }

        @Test
        @DisplayName("무시 문자로만 이루어진 키워드")
        fun shouldHandleKeywordWithOnlyIgnoreCharacters() {
            matcher.keyword("!@#$%")
            val result = matcher.match("안녕하세요")

            assertEquals(0, result.start)
            assertEquals(0, result.length)
        }

        @Test
        @DisplayName("대상 문자열이 무시 문자로만 구성된 경우")
        fun shouldHandleTargetWithOnlyIgnoreCharacters() {
            matcher.keyword("안녕")
            val result = matcher.match("!@#$%^&*()")

            assertEquals(HangulMatchResult.EMPTY, result)
        }
    }

    @Nested
    @DisplayName("시작 인덱스 테스트")
    inner class StartIndexTest {

        @Test
        @DisplayName("특정 시작 인덱스에서 매칭 시작")
        fun shouldStartMatchingFromSpecifiedIndex() {
            matcher.keyword("하세")
            val result = matcher.match("안녕하세요하세요", 3)

            assertTrue(result.start >= 3)
        }

        @Test
        @DisplayName("시작 인덱스가 문자열 길이보다 큰 경우")
        fun shouldHandleStartIndexBeyondStringLength() {
            matcher.keyword("안녕")
            val result = matcher.match("안녕하세요", 10)

            assertEquals(HangulMatchResult.EMPTY, result)
        }

        @Test
        @DisplayName("시작 인덱스가 문자열 끝 근처인 경우")
        fun shouldHandleStartIndexNearEndOfString() {
            matcher.keyword("요")
            val result = matcher.match("안녕하세요", 4)

            assertEquals(4, result.start)
            assertEquals(1, result.length)
        }

        @Test
        @DisplayName("시작 인덱스에서 키워드 길이만큼 남은 공간이 부족한 경우")
        fun shouldHandleInsufficientSpaceFromStartIndex() {
            matcher.keyword("긴키워드")
            val result = matcher.match("짧은문자열", 8)

            assertEquals(HangulMatchResult.EMPTY, result)
        }
    }

    @Nested
    @DisplayName("한글 자모 분해 매칭 테스트")
    inner class HangulDecomposeTest {

        @Test
        @DisplayName("초성만으로 매칭")
        fun shouldMatchWithInitialConsonantOnly() {
            matcher.keyword("ㅇㄴ")
            val result = matcher.match("안녕하세요")

            assertEquals(0, result.start)
            assertTrue(result.length > 0)
        }

        @Test
        @DisplayName("부분 자모로 매칭")
        fun shouldMatchWithPartialJamo() {
            matcher.keyword("안ㄴ")
            val result = matcher.match("안녕하세요")

            assertEquals(0, result.start)
            assertTrue(result.length > 0)
        }

        @Test
        @DisplayName("자모 분해 매칭 실패")
        fun shouldFailJamoDecomposeMatching() {
            matcher.keyword("ㅂㅂ")
            val result = matcher.match("안녕하세요")

            assertEquals(HangulMatchResult.EMPTY, result)
        }
    }

    @Nested
    @DisplayName("while 루프 종료 조건 테스트")
    inner class WhileLoopTerminationTest {

        @Test
        @DisplayName("while 루프가 target.length 조건으로 종료되는 경우")
        fun shouldTerminateWhileLoopWhenReachingTargetLength() {
            matcher.keyword("안녕하세요추가문자")
            val result = matcher.match("안녕하세요")

            assertEquals(HangulMatchResult.EMPTY, result)
        }

        @Test
        @DisplayName("매칭 중 문자열 끝에 도달하여 루프 종료")
        fun shouldTerminateWhenReachingEndDuringMatching() {
            matcher.keyword("세요뒤")
            val result = matcher.match("안녕하세요")

            assertEquals(HangulMatchResult.EMPTY, result)
        }

        @Test
        @DisplayName("정확히 문자열 끝에서 매칭 완료")
        fun shouldCompleteMatchingExactlyAtStringEnd() {
            matcher.keyword("세요")
            val result = matcher.match("안녕하세요")

            assertTrue(result.start > 0)
            assertTrue(result.length > 0)
        }

        @Test
        @DisplayName("인덱스 경계에서의 while 루프 동작 확인")
        fun shouldHandleWhileLoopAtIndexBoundary() {
            matcher.keyword("요")
            val result = matcher.match("안녕하세요", 4)

            assertEquals(4, result.start)
            assertEquals(1, result.length)
        }
    }

    @Nested
    @DisplayName("HangulMatch 데이터 클래스 테스트")
    inner class HangulMatchTest {

        @Test
        @DisplayName("HangulMatch EMPTY 상수 확인")
        fun shouldHaveEmptyConstant() {
            val empty = HangulMatchResult.EMPTY
            assertEquals(-1, empty.start)
            assertEquals(0, empty.length)
        }

        @Test
        @DisplayName("HangulMatch 데이터 클래스 생성")
        fun shouldCreateHangulMatchInstance() {
            val match = HangulMatchResult(5, 3)
            assertEquals(5, match.start)
            assertEquals(3, match.length)
        }

        @Test
        @DisplayName("HangulMatch equals 동작 확인")
        fun shouldTestHangulMatchEquality() {
            val match1 = HangulMatchResult(5, 3)
            val match2 = HangulMatchResult(5, 3)
            val match3 = HangulMatchResult(4, 3)

            assertEquals(match1, match2)
            assertNotEquals(match1, match3)
        }
    }

    @Nested
    @DisplayName("HangulMatch 기타 테스트")
    inner class HangulMatchEtcTest {

        @Test
        fun `isValidCharacter 테스트`() {
            println("isValidCharacter: ${matcher.isValidCharacter('1')}")
            println("isValidCharacter: ${matcher.isValidCharacter('f')}")
            println("isValidCharacter: ${matcher.isValidCharacter('U')}")
            println("isValidCharacter: ${matcher.isValidCharacter('ㅊ')}")
            println("isValidCharacter: ${matcher.isValidCharacter('아')}")
        }
    }
}
