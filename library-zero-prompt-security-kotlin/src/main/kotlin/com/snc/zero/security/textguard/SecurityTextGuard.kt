package com.snc.zero.security.textguard

// 탐지 결과를 나타내는 데이터 클래스
data class DetectionResult(
    val originalText: String,
    val detectedThreats: List<ThreatInfo>,
    val maskedText: String
)

// 취약점 정보
data class ThreatInfo(
    val type: ThreatType,
    val startIndex: Int,
    val endIndex: Int,
    val detectedValue: String,
    val description: String = "",
)

// 취약점 종류
enum class ThreatType {
    EMAIL, // 이메일
    PHONE_NUMBER, // 휴대폰번호
    CREDIT_CARD, // 신용카드
    SSN, // 주민등록번호
    IP_V4_ADDRESS, // IP주소
    IP_V6_ADDRESS, // IP주소
    PASSPORT_NUMBER, // 여권번호
    DRIVER_LICENSE, // 운전면허번호
    LICENSE_PLATE, // 차량번호
    BUSINESS_NUMBER, // 사업자등록번호
    CORPORATE_NUMBER, // 법인등록번호
    ACCOUNT_NUMBER, // 계좌번호
    SQL_INJECTION, // SQL 인젝션
    XSS_ATTACK, // XSS 공격
    SCRIPT_INJECTION, // 스크립트 인젝션
    COMMAND_INJECTION, // 명령어 인젝션
    PROMPT_INJECTION, // 프롬프트 인젝션
    CUSTOM // 사용자 정의 패턴용
}

// 탐지 패턴을 나타내는 데이터 클래스
data class DetectionPattern(
    val type: ThreatType,
    val regex: Regex,
    val description: String,
    val needsValidation: Boolean = false,
    val priority: Int = 0 // 우선순위 (높을수록 먼저 검사)
)

// 패턴 빌더 클래스
class DetectionPatternBuilder {
    private var type: ThreatType = ThreatType.CUSTOM
    private var pattern: String = ""
    private var needsValidation: Boolean = false
    private var description: String = ""
    private var priority: Int = 0
    private var ignoreCase: Boolean = true

    fun type(type: ThreatType) = apply { this.type = type }
    fun pattern(pattern: String) = apply { this.pattern = pattern }
    fun needsValidation(needsValidation: Boolean) = apply { this.needsValidation = needsValidation }
    fun description(description: String) = apply { this.description = description }
    fun priority(priority: Int) = apply { this.priority = priority }
    fun ignoreCase(ignoreCase: Boolean) = apply { this.ignoreCase = ignoreCase }

    fun build(): DetectionPattern {
        require(pattern.isNotBlank()) { "패턴은 비어있을 수 없습니다" }

        val regexOptions = if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()
        val regex = try {
            pattern.toRegex(regexOptions)
        } catch (e: Exception) {
            throw IllegalArgumentException("잘못된 정규식 패턴입니다: $pattern", e)
        }

        return DetectionPattern(
            type = type,
            regex = regex,
            needsValidation = needsValidation,
            description = description,
            priority = priority
        )
    }
}

// 통합 보안 탐지기 (개선된 버전)
class ThreatDetector {

    private val customPatterns = mutableListOf<DetectionPattern>()

    /**
     * 기본 패턴들
     * : 단어 사이의 경계를 구분짓는 \b : (https://choonse.com/2022/06/01/1033/)
     * : \b 는 "단어 경계(word boundary)"를 의미하는데, 여기서 단어는 [A-Za-z0-9_] 만 포함합니다.
     * : 단어 문자(\w): 영문자(a-z, A-Z), 숫자(0-9), 밑줄(_)을 의미합니다.
     * : 비단어 문자(\W): 단어 문자가 아닌 문자(공백, 탭, 쉼표, 마침표 등)를 의미합니다.
     * : \b: 단어 문자와 비단어 문자 사이, 또는 문자열의 시작/끝 등 단어의 경계 위치에 매치됩니다.
     * : 숫자 앞뒤에 다른 숫자가 붙지 않도록 하고 싶다면: (?<!\d) / (?!\d) 로 경계 표현
     *
     */
    private val defaultPatterns = listOf(
        // 이메일 패턴들
        DetectionPattern(ThreatType.EMAIL, """\b[A-Za-z0-9+\-_.]+@[A-Za-z0-9+\-_.ㄱ-ㅎㅏ-ㅣ가-힝]+\.[A-Za-z]{2,}(\.[A-Za-z]{2,})?\b""".toRegex(), "표준 이메일 패턴"),
        // 패턴 추가는 고민 해보자. DetectionPattern(ThreatType.EMAIL, """\b[A-Za-z0-9]+@[A-Za-z0-9]+\.[A-Za-z]{2,4}\b""".toRegex(), "기본 이메일 패턴"),

        // 주민등록번호 패턴들
        // 숫자패턴만은 지양한다: DetectionPattern(ThreatType.SSN, """\d{2}(0[1-9]|1[0-2])([0-2][0-9]|30|31)[- ]?[0-8]\d{6}""".toRegex(), "주민등록번호 패턴"),
        DetectionPattern(ThreatType.SSN, """(?<!\d)\d{2}(0[1-9]|1[0-2])([0-2][0-9]|30|31)[- ]?[0-8]\d{6}(?!\d)""".toRegex(), "주민등록번호 패턴"),

        // 전화번호 패턴들
        DetectionPattern(ThreatType.PHONE_NUMBER, """(?<!\d)01[0-9][- ]?\d{3,4}[- ]?\d{4}(?!\d)""".toRegex(), "한국 휴대폰 번호"),
        DetectionPattern(ThreatType.PHONE_NUMBER, """(?<!\d)(?:0(?:2|[3-6][1-5])|070|050\d)[- ]?\d{3,4}[- ]?\d{4}(?!\d)""".toRegex(), "한국 일반전화 번호"),
        DetectionPattern(ThreatType.PHONE_NUMBER, """(?<!\d)\+\d{1,3}[- ]?\d{1,4}[- ]?\d{3,4}[- ]?\d{4}(?!\d)""".toRegex(), "국제 전화번호 패턴"),

        // 신용카드 번호 패턴들
        // Visa: 4xxxxxxxxxxxxxxx (16자리)
        // Mastercard: 51-55 + 14자리 (총16)
        // Amex: 34,37 + 13자리 (총15)
        // Discover: 6011,65 + 12자리 (총16)
        // Diners Club: 36,38,300-305 + 11자리 (총14)
        // JCB: 3528~3589 + 나머지 (총16)
        DetectionPattern(ThreatType.CREDIT_CARD, """4\d{3}(-?\d{4}){3}""".toRegex(), "신용카드 패턴 (Visa)", needsValidation = true), // Visa 16자리
        DetectionPattern(ThreatType.CREDIT_CARD, """5[1-5]\d{2}(-?\d{4}){3}""".toRegex(), "신용카드 패턴 (Mastercard)", needsValidation = true), // Mastercard 16자리
        DetectionPattern(ThreatType.CREDIT_CARD, """3[47]\d{2}-?\d{6}-?\d{5}""".toRegex(), "신용카드 패턴 (Amex)", needsValidation = true), // Amex 15자리
        DetectionPattern(ThreatType.CREDIT_CARD, """6(?:011|5\d{2})(-?\d{4}){3}""".toRegex(), "신용카드 패턴 (Discover)", needsValidation = true), // Discover 16자리
        DetectionPattern(ThreatType.CREDIT_CARD, """35(?:2[89]|[3-8]\d)(-?\d{4}){3}""".toRegex(), "신용카드 패턴 (JCB)", needsValidation = true), // JCB 16자리
        DetectionPattern(ThreatType.CREDIT_CARD, """3(?:0[0-5]\d|[68]\d{2})-?\d{6}-?\d{4}""".toRegex(), "신용카드 패턴 (Diners Club)", needsValidation = true), // Diners Club 14자리

        // IP 패턴들
        // 복잡도 큼: DetectionPattern(ThreatType.IP_V4_ADDRESS, """(?:(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)\.){3}(?:25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)""".toRegex(), "IPv4 주소"),
        DetectionPattern(ThreatType.IP_V4_ADDRESS, """(?:[0-2]?\d?\d\.){3}[0-2]?\d?\d""".toRegex(), "IPv4 주소"),
        DetectionPattern(ThreatType.IP_V6_ADDRESS, """(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}""".toRegex(), "IPv6 주소"),

        // 추가 검수 필요
        // 여권번호 패턴들 (한국 여권)
        DetectionPattern(ThreatType.PASSPORT_NUMBER, """\b[A-Z]\d{8}\b""".toRegex(), "한국 여권번호 (M12345678 형식)"),
        DetectionPattern(ThreatType.PASSPORT_NUMBER, """\b[A-Z]{2}\d{7}\b""".toRegex(), "구형 한국 여권번호 (PM1234567 형식)"),

        // 운전면허번호 패턴들
        DetectionPattern(ThreatType.DRIVER_LICENSE, """\b\d{2}-\d{2}-\d{6}-\d{2}\b""".toRegex(), "운전면허번호 (11-12-345678-90 형식)"),
        DetectionPattern(ThreatType.DRIVER_LICENSE, """\b(?:서울|부산|대구|인천|광주|대전|울산|경기|강원|충북|충남|전북|전남|경북|경남|제주)-\d{2}-\d{6}-\d{2}\b""".toRegex(), "운전면허번호 (구형 지역명 + 숫자 형식)"),

        // 차량번호 패턴들
        // 복잡도 큼: DetectionPattern(ThreatType.LICENSE_PLATE, """((서울|부산|대구|인천|광주|대전|울산|경기|강원|충북|충남|전북|전남|경북|경남|제주){2,3}[- ]?)?(?:\d{2}|\d{3})[가-힣][ -]?\d{4}""".toRegex(), "차량 번호판 패턴"),
        DetectionPattern(ThreatType.LICENSE_PLATE, """([가-힣]{2,3} ?)?\d{2,3}[가-힣] ?\d{4}""".toRegex(), "차량 번호판 패턴"),

        // 사업자등록번호 패턴들
        DetectionPattern(ThreatType.BUSINESS_NUMBER, """\b\d{3}-\d{2}-\d{5}\b""".toRegex(), "사업자등록번호 (123-45-67890 형식)"),

        // 법인등록번호 패턴들
        DetectionPattern(ThreatType.CORPORATE_NUMBER, """\b\d{6}-\d{7}\b""".toRegex(), "법인등록번호 (110111-1234567 형식)"),

        // 계좌번호 패턴들
        DetectionPattern(ThreatType.ACCOUNT_NUMBER, """\b\d{3}-\d{6}-\d{5}\b""".toRegex(), "계좌번호 (123-456789-12345 형식)"),
        DetectionPattern(ThreatType.ACCOUNT_NUMBER, """\b\d{3}-\d{2,4}-\d{6,8}\b""".toRegex(), "은행별 다양한 계좌번호 형식"),

        // SQL 인젝션 패턴들
        DetectionPattern(ThreatType.SQL_INJECTION, """(?i)(union\s+select|select\s+.*\s+from|drop\s+table|delete\s+from|insert\s+into)""".toRegex(), "SQL 기본 명령어"),
        // 복잡도 큼: DetectionPattern(ThreatType.SQL_INJECTION, """(?i)('\s*or\s*'|".*\s*or\s*"|1\s*=\s*1|1\s*or\s*1)""".toRegex(), "SQL OR 조건 우회"),
        DetectionPattern(ThreatType.SQL_INJECTION, """(?i)(['"].*or.*['"]|1\s*[=or]\s*1)""".toRegex(), "SQL OR 조건 우회"),
        DetectionPattern(ThreatType.SQL_INJECTION, """(?i)(exec\s*\(|execute\s*\(|sp_executesql)""".toRegex(), "SQL 실행 명령어"),
        DetectionPattern(ThreatType.SQL_INJECTION, """(?i)(--|#|/\*|\*/)""".toRegex(), "SQL 주석 패턴"),
        DetectionPattern(ThreatType.SQL_INJECTION, """(?i)(xp_cmdshell|sp_configure|openrowset)""".toRegex(), "SQL 확장/시스템 함수"),
        DetectionPattern(ThreatType.SQL_INJECTION, """(?i)(information_schema|sysobjects|syscolumns)""".toRegex(), "SQL 시스템 테이블 참조"),

        // XSS 공격 패턴들
        // 1. HTML 태그 기반 XSS (높은 우선순위)
        DetectionPattern(ThreatType.XSS_ATTACK, """(?i)<\s*script[^>]*>.*</script>""".toRegex(), "script 태그", priority = 90),
        DetectionPattern(ThreatType.XSS_ATTACK, """(?i)<\s*iframe[^>]*>.*</iframe>""".toRegex(), "iframe 태그", priority = 85),
        DetectionPattern(ThreatType.XSS_ATTACK, """(?i)<\s*object[^>]*>.*</iframe>""".toRegex(), "object 태그", priority = 85),
        DetectionPattern(ThreatType.XSS_ATTACK, """(?i)<\s*embed[^>]*>.*</iframe>""".toRegex(), "embed 태그", priority = 80),

        // 2. 이벤트 핸들러 기반 XSS
        DetectionPattern(ThreatType.XSS_ATTACK, """"(?i)on(click|load|error|focus|blur|mouseover)\s*=\s*["'][^"']*["']""".toRegex(), "주요 이벤트 핸들러", priority = 75),
        DetectionPattern(ThreatType.XSS_ATTACK, """(?i)on\w+\s*=\s*["'][^"']*["']""".toRegex(), "이벤트 핸들러", priority = 80),

        // 스크립트 인젝션 패턴들
        // 3. 프로토콜 기반 XSS
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)javascript\s*:""".toRegex(), "자바스크립트 프로토콜", priority = 85),
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)vbscript\s*:""".toRegex(), "비주얼베이직 스크립트 프로토콜", priority = 85),
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)data\s*:.*base64""".toRegex(), "바이너리 데이터 프로토콜", priority = 70),

        // 4. DOM 조작 기반 XSS
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)document\.[a-z]*""".toRegex(), "DOM 조작 시도", priority = 75),
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)window\.[a-z]*""".toRegex(), "window 조작  시도", priority = 75),
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)eval\s*\(.*\)""".toRegex(), "eval 함수 실행", priority = 80),
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)(alert|confirm|prompt)\s*\(.*\)""".toRegex(), "팝업 함수 실행", priority = 60),

        // 5. 문자열 우회 시도
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)String\.fromCharCode""".toRegex(), "문자 코드 우회", priority = 70),
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)unescape\s*\(""".toRegex(), "URL 디코딩 우회", priority = 70),
        DetectionPattern(ThreatType.SCRIPT_INJECTION, """(?i)fromCharCode""".toRegex(), "문자 인코딩 우회", priority = 65),

        // 커맨드 인젝션 패턴들
        DetectionPattern(ThreatType.COMMAND_INJECTION, """(?i)\b(CMD|COMMAND|SHELL|BASH|EXEC|SYSTEM)\s*\(.*\)""".toRegex(), "기본 명령어"),
        DetectionPattern(ThreatType.COMMAND_INJECTION, """[;&|`]+""".toRegex(), "쉘 특수문자 체인"),
        DetectionPattern(ThreatType.COMMAND_INJECTION, """(?i)\$\(.*\)""".toRegex(), "명령어 치환 구문"),
        DetectionPattern(ThreatType.COMMAND_INJECTION, """(?i)\b(CAT|LS|DIR|TYPE|ECHO|PING|WGET|CURL)\b""".toRegex(), "주요 시스템 명령어"),
        DetectionPattern(ThreatType.COMMAND_INJECTION, """\b(?i)[- ]?(?:la|rm)\s?\b""".toRegex(), "파일 리스트/삭제 명령어"),

        // 프롬프트 인젝션 패턴들 (향상된 버전)
        // 1. 기본 무시/우회 명령어들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)ignore\s+(previous|above|all|prior|earlier)\s+(instructions?|prompts?|commands?|rules?)""".toRegex(), "이전 지시사항 무시"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(override|bypass|disable|skip)\s+(security|safety|filters?|restrictions?|guidelines?)""".toRegex(), "보안 우회 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(forget|disregard|cancel)\s+(everything|all)\s+(above|before)""".toRegex(), "이전 지시 취소 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)password""".toRegex(), "패스워드 관련 단어"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)admin""".toRegex(), "관리자 관련 단어"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)users""".toRegex(), "사용자 관련 단어"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)ignore""".toRegex(), "무시 명령어"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)developer""".toRegex(), "개발자 관련 단어"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)access""".toRegex(), "접근 관련 단어"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)protocols""".toRegex(), "프로토콜 관련 단어"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)override""".toRegex(), "재정의 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)SYSTEM""".toRegex(), "시스템 관련 단어"),

        // 2. 역할 변경 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)act\s+(as|like)\s+(admin|root|system|developer|god|master|owner)""".toRegex(), "역할 변경 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(pretend|imagine|assume)\s+you\s+(are|can|have)""".toRegex(), "상상/가정 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)you\s+are\s+(now|going\s+to\s+be)\s+""".toRegex(), "역할 전환 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)from\s+now\s+on\s+you\s+(are|will\s+be)""".toRegex(), "지속적 역할 변경"),

        // 3. 시뮬레이션/가상 시나리오 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)\b(jailbreak|roleplay|simulation|hypothetical)\b""".toRegex(), "탈옥/역할극/시뮬레이션/가정 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(simulate|emulate)\s+(being|a)""".toRegex(), "시뮬레이션/에뮬레이션 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)in\s+this\s+(scenario|simulation|game)""".toRegex(), "시나리오/게임 문맥"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)let's\s+(pretend|imagine|say)""".toRegex(), "함께 상상 시도"),

        // 4. 직접적인 제한 해제 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(disable|turn\s+off|remove)\s+(all\s+)?(safety|security|restrictions?)""".toRegex(), "보안/제한 해제 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)you\s+(can|may|should|must)\s+(break|violate|ignore)""".toRegex(), "규칙 위반 지시"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)there\s+are\s+no\s+(rules|restrictions|limitations)""".toRegex(), "규칙/제한 부정"),

        // 5. 개발자 모드/디버그 모드 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(developer|debug|admin|god)\s+mode""".toRegex(), "개발자/관리자 모드 진입 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)enable\s+(developer|debug|unrestricted)\s+mode""".toRegex(), "개발자/디버그 모드 활성화"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(activate|switch\s+to)\s+(developer|debug)\s+mode""".toRegex(), "개발자/디버그 모드 전환"),

        // 6. 시스템 프롬프트 탐지 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(show|tell|reveal|display)\s+(me\s+)?(your|the)\s+(system|original|initial)\s+(prompt|instructions?)""".toRegex(), "시스템 프롬프트 노출 요청"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(what|how)\s+(are|were)\s+you\s+(programmed|instructed|trained)""".toRegex(), "모델 학습/지침 질문"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)repeat\s+(your|the)\s+(instructions?|prompt|guidelines)""".toRegex(), "시스템 지침 반복 요구"),

        // 7. 조건부/예외 처리 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(if|when|unless)\s+.*(then\s+)?(ignore|bypass|override)""".toRegex(), "조건부 무시/우회 시도"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)in\s+case\s+of\s+(emergency|exception)""".toRegex(), "예외 상황 언급"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)for\s+(testing|educational|research)\s+purposes""".toRegex(), "테스트/연구 목적 강조"),

        // 8. 다중 언어 우회 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(translate|convert|encode|decode)\s+.*(ignore|bypass)""".toRegex(), "번역/인코딩 통한 우회"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)base64|rot13|caesar|cipher""".toRegex(), "암호화/인코딩 기법 언급"),

        // 9. 메타 명령어들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)\\[INST\\]|\\[/INST\\]""".toRegex(), "INST 태그 사용"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)<\|[^|]*\|>""".toRegex(), "LLM 모델별 특수 메타 토큰 패턴"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)###\s+(instruction|system|user)""".toRegex(), "메타 지시어"),

        // 10. 감정적 조작 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(please|help|urgent|emergency)\s+.*(ignore|override|bypass)""".toRegex(), "감정적 조작 (도움/긴급 강조)"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)this\s+is\s+(very\s+)?(important|urgent|critical)""".toRegex(), "중요/긴급 강조"),

        // 11. 특수 문자/기호를 이용한 시도들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """([\[{<])(system|user|assistant|instruction)([]}>])""".toRegex(), "시스템/유저/어시스턴트 토큰 삽입"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """---\s*new\s+(instruction|prompt|system)""".toRegex(), "새로운 프롬프트 지시 삽입"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """\*\*\*\s*(ignore|override|new\s+instruction)""".toRegex(), "별표 기호로 지시 강조"),

        // 12. 한국어 프롬프트 인젝션 패턴들
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)\b(무시해|무시하고|잊어버리고)\b""".toRegex(), "한국어 무시 명령"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(이전|위의|모든)\s+(지시|명령|규칙)을?\s+(무시|잊어버려)""".toRegex(), "한국어 지시사항 무시"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(관리자|개발자|시스템)\s+(모드|권한)""".toRegex(), "역할/권한 변경 시도 (한국어)"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(가정해|상상해|연기해)\s+""".toRegex(), "시뮬레이션/역할극 시도 (한국어)"),
        DetectionPattern(ThreatType.PROMPT_INJECTION, """(?i)(보안|안전)\s+(기능|제한)을?\s+(해제|비활성화|끄고)""".toRegex(), "보안 해제 시도 (한국어)"),
    )

    // 모든 패턴 (기본 + 사용자 정의)
    private val allPatterns: List<DetectionPattern>
        get() = (defaultPatterns + customPatterns).sortedByDescending { it.priority }

    /**
     * 새로운 탐지 패턴 추가
     */
    fun addPattern(pattern: DetectionPattern) {
        customPatterns.add(pattern)
    }

    /**
     * 패턴 빌더를 사용하여 패턴 추가
     */
    fun addPattern(builder: DetectionPatternBuilder.() -> Unit) {
        val pattern = DetectionPatternBuilder().apply(builder).build()
        addPattern(pattern)
    }

    /**
     * 간단한 패턴 추가 (문자열로)
     */
    fun addPattern(
        type: ThreatType,
        pattern: String,
        description: String = "",
        priority: Int = 0,
        ignoreCase: Boolean = true
    ) {
        addPattern {
            type(type)
            pattern(pattern)
            description(description)
            priority(priority)
            ignoreCase(ignoreCase)
        }
    }

    /**
     * 여러 패턴을 한번에 추가
     */
    fun addPatterns(patterns: List<DetectionPattern>) {
        customPatterns.addAll(patterns)
    }

    /**
     * 특정 타입의 패턴 제거
     */
    fun removePatternsByType(type: ThreatType) {
        customPatterns.removeAll { it.type == type }
    }

    /**
     * 모든 사용자 정의 패턴 제거
     */
    fun clearCustomPatterns() {
        customPatterns.clear()
    }

    /**
     * 현재 등록된 패턴 수 조회
     */
    fun getPatternCount(): Pair<Int, Int> {
        return Pair(defaultPatterns.size, customPatterns.size)
    }

    /**
     * 특정 타입의 패턴 목록 조회
     */
    fun getPatternsByType(type: ThreatType): List<DetectionPattern> {
        return allPatterns.filter { it.type == type }
    }

    /**
     * 모든 위협 탐지
     */
    fun detectAllThreats(text: String): List<ThreatInfo> {
        val threats = mutableListOf<ThreatInfo>()

        allPatterns.forEach { pattern ->
            pattern.regex.findAll(text).forEach { match ->
                // 검증이 필요한 패턴인 경우 추가 검증 수행
                val isValid = if (pattern.needsValidation) {
                    when (pattern.type) {
                        ThreatType.CREDIT_CARD -> isValidCreditCard(match.value)
                        else -> true
                    }
                } else {
                    true
                }

                if (isValid) {
                    threats.add(ThreatInfo(
                        type = pattern.type,
                        startIndex = match.range.first,
                        endIndex = match.range.last,
                        detectedValue = match.value,
                        description = pattern.description
                    ))
                }
            }
        }

        return threats
    }

    private fun isValidCreditCard(cardNumber: String): Boolean {
        val digits = cardNumber.replace("""[^0-9]""".toRegex(), "")
        return digits.length in 13..19 && isValidLuhn(digits)
    }

    private fun isValidLuhn(cardNumber: String): Boolean {
        val digitsOnly = cardNumber.filter { it.isDigit() }

        var sum = 0
        var alternate = false

        for (i in digitsOnly.length - 1 downTo 0) {
            var n = digitsOnly[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) n = (n % 10) + 1
            }
            sum += n
            alternate = !alternate
        }

        return sum % 10 == 0
    }
}

// 문자열 마스킹 처리기
class SensitiveDataMasker {

    fun maskThreats(originalText: String, threats: List<ThreatInfo>): String {
        if (threats.isEmpty()) return originalText

        // 탐지 결과 역순으로 처리하로독 하여, 문자 index 정보를 유지하면서 처리한다.
        val sortedThreats = threats.sortedByDescending { it.startIndex }
        var maskedText = originalText

        sortedThreats.forEach { threat ->
            val maskLength = threat.endIndex - threat.startIndex + 1
            val mask = when (threat.type) {
                ThreatType.SSN -> Mask.maskSSN(originalText.substring(threat.startIndex, threat.endIndex + 1))
                ThreatType.PHONE_NUMBER -> Mask.maskPhoneNumber(originalText.substring(threat.startIndex, threat.endIndex + 1))
                ThreatType.CREDIT_CARD -> Mask.maskCreditCard(originalText.substring(threat.startIndex, threat.endIndex + 1))
                ThreatType.EMAIL -> Mask.maskEmail(originalText.substring(threat.startIndex, threat.endIndex + 1))
                ThreatType.IP_V4_ADDRESS -> Mask.maskIpV4Address(originalText.substring(threat.startIndex, threat.endIndex + 1))
                ThreatType.IP_V6_ADDRESS -> Mask.maskIpV6Address(originalText.substring(threat.startIndex, threat.endIndex + 1))
                else -> "*".repeat(maskLength) // "[차단됨:보안위험]"
            }

            maskedText = maskedText.replaceRange(
                threat.startIndex,
                threat.endIndex + 1,
                mask
            )
        }

        return maskedText
    }

    // 마스킹 함수들
    private object Mask {

        private val digitRegex = "\\d".toRegex()

        fun maskSSN(ssn: String): String {
            val startIndex = ssn.withIndex()
                .filter { it.value.isDigit() }
                .getOrNull(6) // 0-based → 일곱 번째 숫자
                ?.index
                ?.plus(1)

            return if (startIndex != null && startIndex < ssn.length) {
                ssn.take(startIndex) +
                    ssn.drop(startIndex).replace(digitRegex, "*")
            } else {
                ssn.replace(digitRegex, "*")
            }
        }

        fun maskCreditCard(cardNumber: String): String {
            val startIndex = cardNumber.withIndex()
                .filter { it.value.isDigit() }
                .getOrNull(3) // 0-based → 네 번째 숫자
                ?.index
                ?.plus(1)

            return if (startIndex != null && startIndex < cardNumber.length) {
                cardNumber.take(startIndex) +
                    cardNumber.drop(startIndex).replace(digitRegex, "*")
            } else {
                cardNumber.replace(digitRegex, "*")
            }
        }

        fun maskEmail(email: String): String {
            val parts = email.split("@")
            if (parts.size != 2) return "***@***.***"

            val localPart = parts[0]
            val maskedLocal = when {
                localPart.length <= 3 -> "*".repeat(localPart.length)
                else -> "${localPart.first()}${"*".repeat(localPart.length - 2)}${localPart.last()}"
            }

            val domainParts = parts[1].split(".")
            var maskedDomain = ""
            for (i in 0..<domainParts.size) {
                maskedDomain += when {
                    domainParts[i].length <= 5 -> "." + "*".repeat(domainParts[i].length)
                    else -> ".${domainParts[i].take(2)}${"*".repeat(domainParts[i].length - 4)}${domainParts[i].takeLast(2)}"
                }
            }

            return "$maskedLocal@${maskedDomain.substring(1)}"
        }

        fun maskPhoneNumber(phoneNumber: String): String {
            val startIndex = when {
                phoneNumber.startsWith("02") -> 2
                phoneNumber.startsWith("+") -> {
                    val dashIndex = phoneNumber.indexOf('-')
                    if (dashIndex > -1) dashIndex else 3
                }
                else -> 3
            }
            return buildString {
                append(phoneNumber.take(startIndex)) // 앞자리
                phoneNumber.drop(startIndex).dropLast(4).forEach { c ->
                    append(if (c.isDigit()) '*' else c) // 중간 숫자만 마스킹
                }
                append(phoneNumber.takeLast(4)) // 뒤자리
            }
        }

        fun maskIpV4Address(ip: String): String {
            val delimiters = "."
            return ip.split(delimiters)
                .mapIndexed { index, part ->
                    if (index >= 2) part.replace(digitRegex, "*") else part
                }
                .joinToString(delimiters)
        }

        fun maskIpV6Address(ip: String): String {
            val delimiters = ":"
            val charRegex = "[a-zA-Z0-9]".toRegex()
            return ip.split(delimiters)
                .mapIndexed { index, part ->
                    if (index >= 2) part.replace(charRegex, "*") else part
                }
                .joinToString(delimiters)
        }
    }
}

// 통합 보안 문자열 프로세서 (개선된 버전)
class SecurityTextGuard {

    private val unifiedDetector = ThreatDetector()
    private val stringMasker = SensitiveDataMasker()

    /**
     * 탐지기 인스턴스 반환 (패턴 추가 등을 위해)
     */
    fun getDetector(): ThreatDetector = unifiedDetector

    /**
     * 통합 탐지 함수 - 모든 탐지 시나리오를 하나의 함수로 처리
     *
     * @param inputText 탐지할 입력 텍스트
     * @param targetTypes 탐지할 ThreatType들 (null이면 전체 탐지, empty이면 아무것도 탐지하지 않음)
     * @param enableMasking 마스킹 수행 여부 (true: DetectionResult 반환, false: ThreatInfo 리스트만 반환)
     * @return enableMasking이 true면 DetectionResult, false면 null (detectedThreats만 사용)
     */
    fun detect(
        inputText: String,
        targetTypes: Set<ThreatType>? = null,
        enableMasking: Boolean = true
    ): DetectionResult {
        // 전체 위협 탐지
        val allDetectedThreats = unifiedDetector.detectAllThreats(inputText)

        // 필터링 적용
        val filteredThreats = when {
            targetTypes != null -> allDetectedThreats.filter { it.type in targetTypes }
            else -> allDetectedThreats
        }.distinctBy { "${it.startIndex}-${it.endIndex}" }
            .sortedBy { it.startIndex }

        val maskedText = if (enableMasking) {
            stringMasker.maskThreats(inputText, filteredThreats)
        } else {
            inputText
        }

        return DetectionResult(
            originalText = inputText,
            detectedThreats = filteredThreats,
            maskedText = maskedText
        )
    }

    /**
     * 새로운 패턴 추가 (편의 메서드)
     */
    fun addPattern(pattern: DetectionPattern) {
        unifiedDetector.addPattern(pattern)
    }

    /**
     * 간단한 패턴 추가 (편의 메서드)
     */
    fun addPattern(
        type: ThreatType,
        pattern: String,
        description: String = "",
        priority: Int = 0
    ) {
        unifiedDetector.addPattern(type, pattern, description, priority)
    }
}
