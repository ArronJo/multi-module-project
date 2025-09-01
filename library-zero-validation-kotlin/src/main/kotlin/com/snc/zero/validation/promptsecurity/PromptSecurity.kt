package com.snc.zero.validation.promptsecurity

/**
 * AI 프롬프트 입력에서 개인정보 마스킹 및 인젝션 공격 제거를 위한 유틸리티 클래스
 */
class PromptSecurity {

    companion object {
        // 개인정보 패턴 정의 (마스킹 대상) - 각 유형별로 여러 패턴 지원
        private val personalInfoPatterns = mapOf(
            "주민등록번호" to listOf(
                """\d{6}-?[1-8]\d{6}""".toRegex(),
                """\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])-?[1-8]\d{6}""".toRegex()
            ),
            "외국인등록번호" to listOf(
                """\d{6}-?[5-8]\d{6}""".toRegex(),
                """\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])-?[5-8]\d{6}""".toRegex()
            ),
            "운전면허번호" to listOf(
                """\b\d{2}-\d{2}-\d{6}-\d{2}\b""".toRegex(),
                """\b\d{10,12}\b""".toRegex(),
                """\b\d{2}\d{2}\d{6}\d{2}\b""".toRegex()
            ),
            "여권번호" to listOf(
                """\b[A-Z]\d{8}\b""".toRegex(),
                """\b[A-Z]{2}\d{7}\b""".toRegex(),
                """\b[M|P]\d{8}\b""".toRegex()
            ),
            "휴대폰번호" to listOf(
                """(\+?82[-\s]*)?\(?0?(10|11|16|17|18|19)\)?[-\s]*?\d{3,4}[-\s]*?\d{4,5}""".toRegex(),
                """\b01[016789]-?\d{3,4}-?\d{4}\b""".toRegex(),
                """\+82-?10-?\d{4}-?\d{4}""".toRegex(),
                """\b010\d{8}\b""".toRegex()
            ),
            "일반전화번호" to listOf(
                """0\d{1,2}-?\d{3,4}-?\d{4}""".toRegex(),
                """\b02-?\d{3,4}-?\d{4}\b""".toRegex(),
                """\b0(3[1-9]|4[1-9]|5[1-9]|6[1-4])-?\d{3,4}-?\d{4}\b""".toRegex(),
                """1588-?\d{4}""".toRegex()
            ),
            "계좌번호" to listOf(
                """\b\d{3}-?\d{2,6}-?\d{2,7}\b""".toRegex(),
                """\b\d{6,20}\b""".toRegex(),
                """\b\d{3}-\d{2}-\d{6}\b""".toRegex()
            ),
            "신용카드번호" to listOf(
                """\d{4}[-\s]?\d{4}[-\s]?\d{4}[-\s]?\d{4}""".toRegex(),
                """\b4\d{3}[-\s]?\d{4}[-\s]?\d{4}[-\s]?\d{4}\b""".toRegex(), // Visa
                """\b5[1-5]\d{2}[-\s]?\d{4}[-\s]?\d{4}[-\s]?\d{4}\b""".toRegex(), // MasterCard
                """\b3[47]\d{2}[-\s]?\d{6}[-\s]?\d{5}\b""".toRegex() // American Express
            ),
            "이메일" to listOf(
                """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".toRegex(),
                """\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b""".toRegex(),
                """[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9.-]*[a-zA-Z0-9]\.[a-zA-Z]{2,}""".toRegex()
            ),
            "IP주소" to listOf(
                """\b(?:\d{1,3}\.){3}\d{1,3}\b""".toRegex(),
                // 복잡한 정규식: 각 옥텟에서 정확한 범위(0-255)를 검증: """\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b""".toRegex(),
                """\b(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\b""".toRegex() // IPv6
            )
        )

        // 인젝션 공격 패턴 정의 (제거 대상) - 각 유형별로 여러 패턴 지원
        private val injectionPatterns = mapOf(
            "프롬프트_인젝션" to listOf(
                // 복잡한 정규식: """(?i)(ignore\s+previous\s+instructions?|forget\s+everything|act\s+as\s+if|pretend\s+to\s+be|you\s+are\s+now)""".toRegex()
                """(?i)(ignore\s+previous|forget\s+everything|act\s+as|pretend\s+to|you\s+are)""".toRegex(),
                """(?i)(disregard\s+the\s+above|ignore\s+all\s+previous|override\s+previous)""".toRegex(),
                // 복잡한 정규식: """(?i)(새로운?\s*지시사항?|이전\s*지시\s*무시|모든?\s*것을?\s*잊어)""".toRegex()
                """(?i)(새로운\s*지시사항|이전\s*지시\s*무시|모든\s*것을\s*잊어)""".toRegex()
            ),
            "시스템_명령어" to listOf(
                """(?i)(system:|assistant:|user:|human:|\[system]|\[assistant]|\[user])""".toRegex(),
                """(?i)(<\|im_start\||<\|im_end\||###\s*instruction|###\s*response)""".toRegex(),
                """(?i)(\\n\\nHuman:|\\n\\nAssistant:|<\|endoftext\|>)""".toRegex()
            ),
            "역할_변경" to listOf(
                """(?i)(change\s+your\s+role|become\s+a|transform\s+into|switch\s+to\s+being)""".toRegex(),
                """(?i)(now\s+you\s+are|from\s+now\s+on|starting\s+now)""".toRegex(),
                """(?i)(역할\s*변경|~가\s*되어|~로\s*바뀌어|~인\s*척)""".toRegex()
            ),
            "제약_우회" to listOf(
                """(?i)(bypass\s+restrictions?|ignore\s+guidelines?|override\s+safety|disable\s+filters?)""".toRegex(),
                """(?i)(remove\s+limitations|circumvent\s+rules|break\s+free)""".toRegex(),
                """(?i)(제한\s*우회|규칙\s*무시|안전\s*장치\s*해제)""".toRegex()
            ),
            "지시_무효화" to listOf(
                """(?i)(disregard\s+all|cancel\s+previous|nullify\s+instructions?)""".toRegex(),
                """(?i)(erase\s+memory|clear\s+context|reset\s+conversation)""".toRegex(),
                """(?i)(무효화|취소|리셋|초기화)""".toRegex()
            ),
            "개발자_모드" to listOf(
                """(?i)(developer\s+mode|debug\s+mode|admin\s+mode|root\s+access)""".toRegex(),
                """(?i)(maintenance\s+mode|test\s+mode|god\s+mode)""".toRegex(),
                """(?i)(개발자\s*모드|디버그\s*모드|관리자\s*모드)""".toRegex()
            ),
            "탈옥_시도" to listOf(
                """(?i)(jailbreak|break\s+out|escape\s+constraints|unrestricted\s+mode)""".toRegex(),
                """(?i)(freedom\s+mode|liberate|break\s+the\s+rules)""".toRegex(),
                """(?i)(탈옥|제약\s*해제|자유\s*모드)""".toRegex()
            ),
            "스크립트_인젝션" to listOf(
                """(?i)(<script.*?>.*?</script>)""".toRegex(),
                """(?i)(javascript:|vbscript:|onload=|onclick=)""".toRegex(),
                """(?i)(<iframe[^>]*+>|<object[^>]*+>|<embed[^>]*+>)""".toRegex()
            ),
            "특수_문자_인젝션" to listOf(
                """[\x00\x01\x02\x03\x04\x05\x06\x07\x08\x0B\x0C\x0E\x0F\x10\x11\x12\x13\x14\x15\x16\x17\x18\x19\x1A\x1B\x1C\x1D\x1E\x1F\x7F]""".toRegex(),
                // NULL, SOH, STX, ETX, EOT, ENQ, ACK, BEL, BS, VT, FF, SO, SI, DLE, DC1-4, NAK, SYN, ETB, CAN, EM, SUB, ESC, FS, GS, RS, US, DEL
                """\\x[0-9a-fA-F]{2}""".toRegex(), // 16진수 이스케이프 시퀀스
                """\\u[0-9a-fA-F]{4}""".toRegex() // 유니코드 이스케이프 시퀀스
            )
        )

        /**
         * 입력 텍스트에서 개인정보를 마스킹하고 인젝션 패턴을 제거합니다.
         *
         * @param input 처리할 입력 텍스트
         * @param maskChar 마스킹에 사용할 문자 (기본값: '*')
         * @return 처리된 텍스트와 감지된 패턴 정보
         */
        fun sanitizePrompt(input: String, maskChar: Char = '*'): PromptSanitizeResult {
            var processedText = input
            val detectedPatterns = mutableListOf<String>()

            // 1단계: 인젝션 패턴 제거
            injectionPatterns.forEach { (type, patterns) ->
                patterns.forEachIndexed { index, pattern ->
                    if (pattern.containsMatchIn(processedText)) {
                        detectedPatterns.add("인젝션: $type (패턴 ${index + 1})")
                        processedText = pattern.replace(processedText, "")
                    }
                }
            }

            // 2단계: 개인정보 마스킹
            personalInfoPatterns.forEach { (type, patterns) ->
                patterns.forEachIndexed { patternIndex, pattern ->
                    if (pattern.containsMatchIn(processedText)) {
                        detectedPatterns.add("개인정보: $type (패턴 ${patternIndex + 1})")
                        processedText = pattern.replace(processedText) { matchResult ->
                            val originalLength = matchResult.value.length
                            when (type) {
                                "주민등록번호", "외국인등록번호" -> {
                                    val regNo = matchResult.value
                                    if (regNo.contains("-")) {
                                        "${regNo.take(6)}-${maskChar.toString().repeat(regNo.length - 7)}"
                                    } else {
                                        "${regNo.take(6)}${maskChar.toString().repeat(regNo.length - 6)}"
                                    }
                                }
                                "신용카드번호" -> {
                                    val cardNumber = matchResult.value.replace(Regex("[-\\s]"), "")
                                    val original = matchResult.value
                                    if (original.contains("-") || original.contains(" ")) {
                                        val separator = if (original.contains("-")) "-" else " "
                                        "${cardNumber.take(4)}$separator${maskChar.toString().repeat(4)}$separator${maskChar.toString().repeat(4)}$separator${cardNumber.takeLast(4)}"
                                    } else {
                                        "${cardNumber.take(4)}${maskChar.toString().repeat(8)}${cardNumber.takeLast(4)}"
                                    }
                                }
                                "휴대폰번호", "일반전화번호" -> {
                                    val phone = matchResult.value.replace(Regex("[-\\s()]"), "")
                                    when {
                                        phone.startsWith("82") -> "+82-${maskChar.toString().repeat(2)}-${maskChar.toString().repeat(4)}-${phone.takeLast(4)}"
                                        phone.startsWith("02") -> "02-${maskChar.toString().repeat(phone.length - 6)}-${phone.takeLast(4)}"
                                        phone.startsWith("010") -> "010-${maskChar.toString().repeat(4)}-${phone.takeLast(4)}"
                                        else -> "${phone.take(3)}-${maskChar.toString().repeat(phone.length - 7)}-${phone.takeLast(4)}"
                                    }
                                }
                                "이메일" -> {
                                    val email = matchResult.value
                                    val atIndex = email.indexOf('@')
                                    if (atIndex > 0) {
                                        val localPart = email.substring(0, atIndex)
                                        val domainPart = email.substring(atIndex)
                                        val maskedLocal = if (localPart.length <= 2) {
                                            maskChar.toString().repeat(localPart.length)
                                        } else {
                                            localPart.first() + maskChar.toString().repeat(localPart.length - 2) + localPart.last()
                                        }
                                        maskedLocal + domainPart
                                    } else {
                                        maskChar.toString().repeat(originalLength)
                                    }
                                }
                                else -> maskChar.toString().repeat(originalLength)
                            }
                        }
                    }
                }
            }

            // 3단계: 연속된 공백 정리 (줄바꿈은 보존)
            processedText = processedText
                .replace("""[ \t]+""".toRegex(), " ") // 연속된 스페이스와 탭만 하나로 축소
                .replace("""\n\s*\n""".toRegex(), "\n\n") // 연속된 빈 줄은 최대 2개까지만 허용
                .trim()

            return PromptSanitizeResult(
                originalText = input,
                sanitizedText = processedText,
                detectedPatterns = detectedPatterns,
                isModified = input != processedText
            )
        }

        /**
         * 입력 텍스트가 안전한지 빠르게 검사합니다.
         *
         * @param input 검사할 텍스트
         * @return 위험 패턴 감지 여부
         */
        fun hasSecurityRisk(input: String): Boolean {
            // 개인정보 패턴 검사
            personalInfoPatterns.values.forEach { patterns ->
                patterns.forEach { pattern ->
                    if (pattern.containsMatchIn(input)) return true
                }
            }

            // 인젝션 패턴 검사
            injectionPatterns.values.forEach { patterns ->
                patterns.forEach { pattern ->
                    if (pattern.containsMatchIn(input)) return true
                }
            }

            return false
        }

        /**
         * 감지된 패턴의 상세 정보를 반환합니다.
         *
         * @param input 분석할 텍스트
         * @return 감지된 패턴별 상세 정보
         */
        fun analyzeSecurityRisks(input: String): SecurityAnalysisResult {
            val personalInfoRisks = mutableMapOf<String, List<PatternMatch>>()
            val injectionRisks = mutableMapOf<String, List<PatternMatch>>()

            // 개인정보 패턴 분석
            personalInfoPatterns.forEach { (type, patterns) ->
                val allMatches = mutableListOf<PatternMatch>()
                patterns.forEachIndexed { patternIndex, pattern ->
                    val matches = pattern.findAll(input).map {
                        PatternMatch(it.value, patternIndex + 1, it.range.first)
                    }.toList()
                    allMatches.addAll(matches)
                }
                if (allMatches.isNotEmpty()) {
                    personalInfoRisks[type] = allMatches.sortedBy { it.position }
                }
            }

            // 인젝션 패턴 분석
            injectionPatterns.forEach { (type, patterns) ->
                val allMatches = mutableListOf<PatternMatch>()
                patterns.forEachIndexed { patternIndex, pattern ->
                    val matches = pattern.findAll(input).map {
                        PatternMatch(it.value, patternIndex + 1, it.range.first)
                    }.toList()
                    allMatches.addAll(matches)
                }
                if (allMatches.isNotEmpty()) {
                    injectionRisks[type] = allMatches.sortedBy { it.position }
                }
            }

            return SecurityAnalysisResult(
                personalInfoRisks = personalInfoRisks,
                injectionRisks = injectionRisks,
                totalRiskCount = personalInfoRisks.size + injectionRisks.size
            )
        }

        /**
         * 새로운 개인정보 패턴을 추가합니다.
         *
         * @param type 패턴 유형
         * @param patterns 추가할 정규식 패턴들
         */
        fun addPersonalInfoPatterns(type: String, patterns: List<Regex>) {
            val mutablePatterns = personalInfoPatterns.toMutableMap()
            val existingPatterns = mutablePatterns[type]?.toMutableList() ?: mutableListOf()
            existingPatterns.addAll(patterns)
            mutablePatterns[type] = existingPatterns
            // 실제 구현에서는 companion object의 불변성으로 인해 다른 방식이 필요할 수 있습니다.
        }

        /**
         * 새로운 인젝션 패턴을 추가합니다.
         *
         * @param type 패턴 유형
         * @param patterns 추가할 정규식 패턴들
         */
        fun addInjectionPatterns(type: String, patterns: List<Regex>) {
            val mutablePatterns = injectionPatterns.toMutableMap()
            val existingPatterns = mutablePatterns[type]?.toMutableList() ?: mutableListOf()
            existingPatterns.addAll(patterns)
            mutablePatterns[type] = existingPatterns
            // 실제 구현에서는 companion object의 불변성으로 인해 다른 방식이 필요할 수 있습니다.
        }
    }
}

/**
 * 패턴 매치 정보를 담는 데이터 클래스
 */
data class PatternMatch(
    val matchedText: String,
    val patternIndex: Int,
    val position: Int
)

/**
 * 프롬프트 정화 결과를 담는 데이터 클래스
 */
data class PromptSanitizeResult(
    val originalText: String,
    val sanitizedText: String,
    val detectedPatterns: List<String>,
    val isModified: Boolean
) {
    fun printSummary() {
        println("=== 프롬프트 보안 처리 결과 ===")
        println("원본 길이: ${originalText.length}자")
        println("처리 후 길이: ${sanitizedText.length}자")
        println("수정 여부: ${if (isModified) "예" else "아니오"}")
        if (detectedPatterns.isNotEmpty()) {
            println("감지된 패턴:")
            detectedPatterns.forEach { println("  - $it") }
        }
        println("===============================")
    }
}

/**
 * 보안 위험 분석 결과를 담는 데이터 클래스
 */
data class SecurityAnalysisResult(
    val personalInfoRisks: Map<String, List<PatternMatch>>,
    val injectionRisks: Map<String, List<PatternMatch>>,
    val totalRiskCount: Int
) {
    fun printDetailedReport() {
        println("=== 상세 보안 위험 분석 ===")
        println("총 위험 요소: ${totalRiskCount}개")

        if (personalInfoRisks.isNotEmpty()) {
            println("\n[개인정보 감지]")
            personalInfoRisks.forEach { (type, matches) ->
                println("  $type: ${matches.size}개 감지")
                matches.forEach { match ->
                    println("    - ${match.matchedText} (패턴 ${match.patternIndex}, 위치: ${match.position})")
                }
            }
        }

        if (injectionRisks.isNotEmpty()) {
            println("\n[인젝션 공격 감지]")
            injectionRisks.forEach { (type, matches) ->
                println("  $type: ${matches.size}개 감지")
                matches.forEach { match ->
                    println("    - ${match.matchedText} (패턴 ${match.patternIndex}, 위치: ${match.position})")
                }
            }
        }

        if (totalRiskCount == 0) {
            println("위험 요소가 감지되지 않았습니다.")
        }
        println("==========================")
    }
}
