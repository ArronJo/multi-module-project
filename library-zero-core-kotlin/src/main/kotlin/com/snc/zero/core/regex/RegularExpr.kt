package com.snc.zero.core.regex

import com.snc.zero.logger.jvm.TLogging
import java.util.LinkedList
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

private val logger = TLogging.logger { }

/**
 * 정규식
 *
 * @author mcharima5@gmail.com
 * @since 2022
 */
class RegularExpr private constructor() {

    /*
        1. 괄호 ()의 용도
            - 그룹화(Grouping):
            괄호는 패턴의 일부를 그룹화하는 데 사용됩니다. 그룹화를 통해 전체 패턴의 특정 부분을 하나의 단위로 처리할 수 있습니다.
            예를 들어, (abc)는 abc라는 문자열을 하나의 그룹으로 취급합니다.

            - 캡처(Capturing):
            괄호 안의 패턴에 매칭되는 부분을 캡처하여 나중에 사용할 수 있게 합니다. 이는 매칭된 결과를 저장하고 참조할 수 있게 해줍니다.
            예를 들어, (\d+)-(\d+)라는 정규식은 두 개의 숫자 그룹을 캡처합니다. "123-456"이라는 문자열에 매칭될 경우, "123"과 "456"을 각각의 그룹에 저장합니다.

            - 역참조(Backreferencing):
            캡처된 그룹은 정규식 내에서 역참조를 통해 다시 사용할 수 있습니다. 역참조는 일반적으로 \1, \2 등의 형식으로 사용됩니다.
            예: (\w)\1는 같은 문자가 두 번 연속으로 나오는 경우를 찾습니다(예: "oo"에서 "o"가 두 번 연속).

        2. 대괄호 []의 용도
            - 문자 클래스(Character Class):
            대괄호는 특정 문자 집합 중 하나에 매칭되는 패턴을 정의하는 데 사용됩니다. 대괄호 안에 포함된 문자 중 하나와 일치합니다.
            예: [abc]는 'a', 'b', 또는 'c' 중 하나에 매칭됩니다.

            - 범위 지정:
            대괄호 안에서는 문자 범위를 지정할 수 있습니다. 하이픈 -을 사용하여 범위를 나타냅니다.
            예: [a-z]는 소문자 알파벳 전체에 매칭됩니다.

            - 부정(Negation):
            대괄호 안에서 캐럿(^)을 사용하면 특정 문자 집합을 제외하는 부정된 문자 클래스를 정의할 수 있습니다.
            예: [^abc]는 'a', 'b', 'c'를 제외한 모든 문자에 매칭됩니다.

        요약
            (): 그룹화와 캡처를 위해 사용되며, 역참조를 통해 정규식 내에서 재사용할 수 있습니다.
            []: 문자 클래스 및 범위 지정을 위해 사용되며, 대괄호 내 문자 중 하나에 매칭됩니다.

     */

    companion object {

        const val PATTERN_PW_ENG_UC_LC_NUM_SPC = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{10,}$"
        const val PATTERN_PW_ENG_NUM_SPC = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{10,}$"

        const val PATTERN_PHONE_NUM = "(?:\\+82-?1[0-9]-?\\d{3,4}-?\\d{4})|(?:01[0-9]-?\\d{3,4}-?\\d{4})"

        const val PATTERN_EMAIL = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"

        const val DIGIT = "0-9"
        const val LETTER_LOWER = "a-z"
        const val LETTER_UPPER = "A-Z"

        const val HANGUL_CHO = "\\u1100-\\u115E\\u3131-\\u314E"
        const val HANGUL_JUNG = "\\u1161-\\u11A7\\u314F-\\u318E\\uD7B0-\\uD7C6"
        const val HANGUL_JONG = "\\u11A8-\\u11FF\\uA960-\\uA97C\\uD7CB-\\uD7FB"
        const val HANGUL_AREA = "\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D"
        const val HANGUL_COMPATIBILITY = "\\uAC00-\\uD7A3"
        const val HANGUL = "$HANGUL_CHO$HANGUL_JUNG$HANGUL_JONG$HANGUL_JUNG$HANGUL_AREA$HANGUL_COMPATIBILITY"
        const val PATTERN_HANGUL = "[$HANGUL]+"

        const val PATTERN_INPUT_CHARS = "[$DIGIT$LETTER_LOWER$LETTER_UPPER\\s$HANGUL]+"

        /**
         * 정규식 조건에 일치하는가?
         */
        fun matches(regularExpression: String, content: String): Boolean {
            return Pattern.matches(regularExpression, content)
        }

        /**
         * 정규식에 해당하는 문자열 추출
         */
        fun find(regularExpression: String, input: String): Array<String> {
            val ret = LinkedList<String>()
            try {
                val pattern = Pattern.compile(regularExpression)
                val matcher = pattern.matcher(input)
                while (matcher.find()) {
                    val txt = matcher.group()
                    if (txt.isNotEmpty()) {
                        ret.add(txt)
                    }
                }
            } catch (e: PatternSyntaxException) {
                logger.error(e) { }
            }
            return ret.toTypedArray()
        }
    }
}
