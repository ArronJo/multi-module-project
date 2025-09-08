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
        "계약", "약계", "계야약",
        "보험 계약정보", "보험계약 조회", "나의 자동이체정보", "미래연금예상액 조회", "보험금신", "사고보험금 신청",
        "사고보험금 내역조회", "사고보험금 서류보완", "일반보험금 신청", "일반보험금 지급내역", "보험금납입",
        "보험료 조회/납입", "납입계좌 변경", "보험계약변경", "건강체변경 신청", "보험수익자 변경 신청",
        "감액/감액완납 신청", "펀드변경 신청", "보험청약관리", "신계약모니터링", "청약 보완처리",
        "건강검진결과 제출", "청약 철회 신청/취소", "증명서/확인서", "안내장 발송내역", "대출관리",
        "보험계약대출 거래내역", "보험계약대출 관리", "보험계약대출 상환", "변액보험계약대출 계정관리", "신용담보대출 관리",
        "퇴직연금 관리", "퇴직연금 계약조회", "적립금 조회", "수익률 조회",
        "적립금 운용현황 조회/변경", "부담금 투입비율 조회/변경", "개인형IRP지급청구", "개인형IRP 계약해지(일시금수령)",
        "개인형IRP 연금수령신청", "개인형IRP 타사로 계약이전", "개인형IRP 공지사항", "개인형IRP 펀드정보 조회",
        "신규고객 등록", "고객동의 관리", "고객정보 조회", "통합 고객정보 조회", "가망고객 목록(가입설계 동의)",
        "가망고객등록·수정", "가망고객 조건검색", "신계약 모니터링 진행상황 조회", "계좌정보 등록·수정", "(C)조력자 신청관리",
        "단체고객 목록 조회", "MY VIP 고객", "VIP 후보 대상 고객", "VIP 대상 관리", "연락불가고객 등록·조회",
        "FP별 신용정보제한 고객", "고객상담 시작", "고객상담 입력", "상담내용 조회", "기계약 현황 조회(주민번호)",
        "계약 정보 조회", "계약별 지급 처리", "고객별 지급 처리", "분할 만기 원장 조회", "FP 안심통화·상담이력 조회",
        "비밀번호 변경", "63 Weekly News", "고객 인사장", "FP 자기소개", "고객 친숙 자료",
        "개척 앙케이트", "구비서류", "제안서 표지", "퇴직연금 활동자료", "계약정보 조회(단체)",
        "계약정보 조회(가입자)", "이율 조회", "우행터 서비스 제도", "우행터 제안서", "우행터 서비스 신청",
        "언더라이팅팀", "메시지 전송", "메시지 전송 내역", "메시지 예약 취소", "모바일 상품권 전송",
        "모바일 상품권 전송 내역", "요금결제 내역", "요금결제", "간편그룹 관리", "표준그룹 관리",
        "이벤트 문자메시지 전송 관리", "택배 신청 메인", "택배 신청 이력 관리", "DM 봉투 발행", "통합 E-mail 발송 관리",
        "E-mail 발송", "E-mail 발송 이력 조회", "영업포털 공통업무 처리", "이미지스캔", "The보장분석",
        "(신용정보원)계약정보 통합 조회", "H-TOPS", "연금 전환 예시", "정보조회동의서 출력", "정보조회동의 SMS 인증",
        "가설동의 신청·동의서 발행", "가설동의 승인관리", "적합성 진단관리", "헬스케어 서비스 신청서 발행", "기준가·수익률",
        "특정기간 운용성과", "계약내용 증번별 관리", "(C)AI펀드추천및상담시스템", "신탁재산 운용 현황", "펀드 가입내역",
        "FP 개인별 관리 펀드", "펀드 수익률 현황", "투자성향별 추천펀드 현황", "증번별(인별) 보장급부 조회", "보종별 보장급부 조회",
        "가입설계", "FP별 설계이력 조회", "종신·정기보험 고객 맞춤제도", "기타 보장성보험 고객 맞춤제도", "청약서 재발행·부속서류 발행",
        "개별 청약서 접수", "기계약 합산 제외 처리", "특약 중도부가 발행", "가상계좌 신청 명세", "간편 실손 재가입 대상 조회",
        "청약 처리현황 조회", "스마트폰 전자청약 진행 현황", "개별 청약서 교체발행 관리", "완전판매 확인(부본·약관 전달)", "신계약 증권 발행(개별 증권)",
        "증권 재발행 처리", "증권 발행 이력(증번)", "계약자용 약관 수신 관리", "외국인 전용상품 설명서 발행", "보험청약 보완 처리",
        "H-PUSS 기관 입력", "H-PUSS 기관 조회", "위험 등급별 가입한도 관리", "검진기관 조회", "진단 수검현황·유효기간 조회",
        "퍼스 기관 입력", "퍼스 기관 조회", "위험 등급별 가입한도 관리", "검진기관 조회", "진단 수검현황·유효기간 조회",
        "건강진단서비스 대상자 조회", "보종별 사정기준 조회", "승낙거절 계약 설명내역 조회", "전환 계약 상세내역 확인", "단체 일괄청약서 발행",
        "단체 일괄청약서 조회", "단체별 잔존계약 현황", "가입설계 접근경로별 일통계 조회", "고객별 부활가능계약 조회", "FP별 부활가능계약 조회",
        "부활가능계약 증번별 연체이자 조회", "부활 청약서 발행", "영수증 선발행 조회·발행", "한화생명금융서비스신계약접수속보", "개인 FP 업적과정 관리",
        "FP 업적·제수당 현황 조회", "FP별 업적추이 조회", "한화생명금융서비스 신인조직,활동,업적속보", "한화라이프랩 신계약 속보", "전자청약부본 통계 조회",
        "FP별 과정관리·유지율 조회", "FP별 유지율 현황", "승낙거절 계약 설명내역 조회", "계약 보험료 수수료 공제내역 조회", "수수료·업적 명세서 내역 조회·발행",
        "FP수수료 지급·회수 현황", "생산성 수수료 현황", "팀장 수수료 지급 명세", "비전 팀장 수수료 지급 명세", "활동기반 구축 지원 현황",
        "FP신분 지점장 수수료 지급 명세", "확보대상 계약 명세 조회", "갱신 계약 명세 조회", "단체 수금유형 명세 조회", "보유계약 연체(해지예상) 명세 조회",
        "양치서비스 관련 조회", "보유계약 명세 조회", "계약별 지급 처리", "계약내용변경 - 계약 사항", "계약내용변경 - 감액 완납",
        "계약내용변경 - 보종 변경", "계약내용변경 - 건강체·표준체", "계약내용변경 - UL 감액 완납", "계약내용변경 - 무사고 계약 전환", "계약내용변경 가능건 통합조회(건강체,태아 등재,지정 대리 청구인,무사고,펀드 관리)",
        "수금 기관별 보험계약대출 연체 관리", "사고보험금 접수·처리 현황", "사고보험금 청구 서류 발행", "납입명세서 발행 명세 조회", "자동납입 이체·미이체 명세 조회",
        "자동납입 연체 계약 명세 조회", "급여 이체 청구·이체 명세 조회", "증권번호 별 월 대체가능 여부 조회", "자동납입 증번별 청구 현황 조회", "월 마감 후 해지 예고·해지계약 현황 명세 조회",
        "기계약 현황 조회(주민번호)", "계약 정보 조회", "기계약 합산 조회", "종합검진 예약 요청·승인", "종합건강검진권 발행",
        "계약자용 약관 조회 E-mail 전송", "11시 콘서트 신청", "종진 실시간 예약현황 조회", "오늘의 이슈고객 확인", "FP 터치 정보 - 고객",
        "잠재고객 등록 관리", "고객정보 정비 대상", "상담 신청 고객 매칭 서비스", "캠페인 활동 관리", "신상품 타겟 고객 관리",
        "마케팅 타겟고객 조회", "완납 만기도래 계약 목록 조회", "완납 만기도래 계약 집계 조회", "보유 고객 현황 관리", "만기 고객 조회",
        "Success List 총괄 현황", "My Success List", "세그맵520", "방문 이슈 일정 조회", "e-Note 입력",
        "e-Note 통계(전체)", "e-Note 조회", "FP 방문활동 관리", "FP 활동 관리", "PC 자료발행 통계",
        "PC 자료발행 목록", "활동성과 통계", "자료발행(실시간)", "보험 사기 제보", "공지사항 조회",
        "공지사항 등록", "공지사항", "모집경력,수집·이용·제공·조회 동의", "소비자보호헌장", "본인 교육 이력",
        "FP 보수교육 이수 관리", "설문 메인", "PC인증 승인 관리"
    )

    private val target = "계약" // 검색어

    /**
     * 문자열 포함 유사도
     * 해당 문자열을 포함하고 있는가? 라는 기준에서는 가장 잘 맞는 유사도가 아닌가 생각한다.
     */
    @Nested
    inner class ContainmentSimilarity {

        val matcher = StringSimilarityMatcher()
        val minSimilarity = 0.3 // 30% 이상만 출력하도록 조건

        @Test
        fun `문자열 포함 여부를 이용한 문자열 매칭 1`() {
            println("=== 문자열 포함 여부 유사도 (최소 $minSimilarity) 출력 ===")
            println("-Usage: findSimilarStrings( ..., SimilarityMethod.CONTAINMENT )")

            println("검색어: $target")
            println()

            val results = matcher.findSimilarStrings(
                data,
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.CONTAINMENT
            )
            results.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }
        }

        @Test
        fun `문자열 포함 여부를 이용한 문자열 매칭 21`() {
            matcher.addData(data)
            println("문자열 포함 결과: ${matcher.findByContainment(data, "계약")}")
            println("문자열 포함 결과: ${matcher.findByContainment(data, "rPdir", 70)}")
            println("문자열 포함 결과: ${matcher.findByContainment("조회")}")
            println("문자열 포함 결과: ${matcher.findByContainment("whghl", 70)}")
        }
    }

    /**
     * 문자 위치 기반
     */
    @Nested
    inner class FindByPositionDifferences {
        val matcher = StringSimilarityMatcher()

        @Test
        fun `(위치 기반) 다른 알파벳 개수 기준 테스트`() {
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

        @Test
        fun `(위치 기반) 다른 알파벳 개수 기준 테스트 (함수 직접 호출)`() {
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
    }

    /**
     * 문자 포함 빈도 이용
     */
    @Nested
    inner class FindByDifferentCharCount {
        val matcher = StringSimilarityMatcher(InMemoryStringDataProvider())

        @BeforeEach
        fun setup() {
            matcher.clearData()
            matcher.addData(data)
        }

        @Test
        fun `(빈도 기반) 다른 알파벳 개수 기준 테스트`() {
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

        @Test
        fun `(빈도 기반) 다른 알파벳 개수 기준 테스트 (함수 직접 호출)`() {
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
    }

    /**
     * 유사도 알고리즘
     */
    @Nested
    inner class SimilarityAlgorithm {

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
        fun `다메라우-레벤슈타인 거리(Levenshtein Distance) 유사도 테스트`() {
            println("=== 레벤슈타인 유사도 (최소 $minSimilarity) 출력 ===")
            println("-Usage: findSimilarStrings( ..., SimilarityMethod.DAMERAU_LEVENSHTEIN )")

            println("검색어: $target")
            println()

            val levenshteinResults = matcher.findSimilarStrings(
                data,
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.DAMERAU_LEVENSHTEIN
            )
            levenshteinResults.forEach { result ->
                println("${result.text}: ${String.format("%.3f", result.similarity)}")
            }

            println("=== 내부 데이터 사용 ===")
            val levenshteinResults2 = matcher.findSimilarStrings(
                target,
                minSimilarity,
                StringSimilarityMatcher.SimilarityMethod.DAMERAU_LEVENSHTEIN
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
        fun `자로 and 자로 웡클러 유사도(Jaro Winkler Similarity) 유사도 테스트 2`() {
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
            println("Jaro-Winkler (threshold=0.5, scale=0.25): ${matcher.jaroWinklerSimilarity(s7, s8, 0.5, 0.25)}")
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
    inner class Etc {

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

        @Test
        fun `Enum entries 테스트`() {
            val e1 = StringSimilarityMatcher.SimilarityMethod.entries.toTypedArray()
            assertEquals(StringSimilarityMatcher.SimilarityMethod.LEVENSHTEIN, e1[0])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.DAMERAU_LEVENSHTEIN, e1[1])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.JACCARD, e1[2])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.COSINE, e1[3])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.JARO, e1[4])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.JARO_WINKLER, e1[5])
            assertEquals(StringSimilarityMatcher.SimilarityMethod.CONTAINMENT, e1[6])

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
