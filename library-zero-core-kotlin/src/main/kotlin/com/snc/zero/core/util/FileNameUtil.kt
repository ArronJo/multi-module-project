package com.snc.zero.core.util

class FileNameUtil {
    companion object {
        // 더 엄격한 문자
        private val REMOVE_CHARS = Regex("[\"'`]")

        // Windows 파일명 사용 불가 문자
        /*
            윈도우 파일명 사용 불가 특수문자 (9종)
            < : 부등호 (작다)
            > : 부등호 (크다)
            : : 콜론 (드라이브 구분자)
            " : 큰따옴표
            / : 슬래시 (경로 구분자)
            \ : 백슬래시 (경로 구분자)
            | : 파이프
            ? : 물음표 (와일드카드)
            별 : 별표 (와일드카드)
         */
        private const val WINDOWS_INVALID_CHARS_PATTERN = "<>:\"/\\\\|?*\\x00-\\x1F"

        // macOS 파일명 사용 불가 문자
        private const val MACOS_INVALID_CHARS_PATTERN = ":/\\x00"

        // Linux 파일명 사용 불가 문자
        private const val LINUX_INVALID_CHARS_PATTERN = "/\\x00"

        // 추가 제한 문자
        private const val ADDITIONAL_UNSAFE_CHARS_PATTERN = ";!,\\[\\]{}()#$&@%\\s"

        // 각 OS별 Regex
        //private val WINDOWS_INVALID_CHARS = Regex("[$WINDOWS_INVALID_CHARS_PATTERN]")
        //private val MACOS_INVALID_CHARS = Regex("[$MACOS_INVALID_CHARS_PATTERN]")
        //private val LINUX_INVALID_CHARS = Regex("[$LINUX_INVALID_CHARS_PATTERN]")
        private val ADDITIONAL_UNSAFE_CHARS = Regex("[$ADDITIONAL_UNSAFE_CHARS_PATTERN]")

        // 통합: 중복 제거하여 조합
        private val ALL_OS_INVALID_CHARS = Regex(
            "[" +
                setOf(
                    WINDOWS_INVALID_CHARS_PATTERN,
                    MACOS_INVALID_CHARS_PATTERN,
                    LINUX_INVALID_CHARS_PATTERN,
                ).joinToString("") { it } +
                "]"
        )

        // 윈도우 예약어
        private val RESERVED_NAMES = setOf(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
        )

        /**
         * 모든 OS에서 안전한 파일명으로 변환
         * @param fileName 원본 파일명
         * @param replacement 대체 문자 (기본값: "_")
         * @param maxLength 최대 길이 (기본값: 255)
         * @return 안전한 파일명
         */
        fun sanitizeFileName(
            fileName: String,
            replacement: String = "_",
            maxLength: Int = 255,
            additional: Boolean = false
        ): String {
            var sanitized = fileName

            // 빈 문자열 체크
            if (sanitized.isEmpty()) return ""

            // 원본이 점으로 시작하는지 확인
            val startsWithDot = sanitized.startsWith(".")

            // 앞쪽 공백 제거, 뒤쪽은 공백과 점 제거
            sanitized = sanitized.trimStart(' ')
            // 원본이 점으로 시작하지 않았다면 앞쪽 점도 제거
            if (!startsWithDot) {
                sanitized = sanitized.trimStart('.')
            }
            sanitized = sanitized.trimEnd(' ', '.')

            // 따옴표 삭제
            sanitized = sanitized.replace(REMOVE_CHARS, "")

            // 금지된 문자 제거
            sanitized = sanitized.replace(ALL_OS_INVALID_CHARS, replacement)

            // 특수문자 제거
            if (additional) {
                sanitized = sanitized.replace(ADDITIONAL_UNSAFE_CHARS, replacement)
            }

            // Windows 예약어 처리
            val nameWithoutExt = sanitized.substringBeforeLast('.')
            val ext = if (sanitized.contains('.')) {
                ".${sanitized.substringAfterLast('.')}"
            } else {
                ""
            }

            if (RESERVED_NAMES.contains(nameWithoutExt.uppercase())) {
                sanitized = "$replacement$nameWithoutExt$ext"
            }

            // 길이 제한 (확장자 포함)
            if (sanitized.length > maxLength) {
                val extension = ext.ifEmpty { "" }
                val maxNameLength = maxLength - extension.length
                sanitized = sanitized.take(maxNameLength) + extension
            }

            // 최종 검증
            if (sanitized.isEmpty() || sanitized == ".") return ""

            return sanitized
        }

        /*
        fun sanitizeFileName(fileName: String?): String {
            // null 또는 빈 문자열 체크
            if (fileName.isNullOrEmpty()) {
                return ""
            }

            // 공백 제거 및 모든 따옴표 제거
            val sanitized = fileName.trim().replace("\"", "").replace("'", "")

            // 확장자 분리
            val lastDot = sanitized.lastIndexOf(".")
            val (name, extension) = if (lastDot > 0) {
                sanitized.substring(0, lastDot) to sanitized.substring(lastDot)
            } else {
                sanitized to ""
            }

            // 파일명에서 안전하지 않은 문자를 언더스코어로 치환
            val cleanedName = name
                .replace("[\\s,;:!?()\\[\\]{}]".toRegex(), "_")  // 특수문자 치환
                .replace("_+".toRegex(), "_")                     // 연속 언더스코어 통합
                .trim('_')                                         // 앞뒤 언더스코어 제거

            return cleanedName + extension
        }
         */
    }
}
