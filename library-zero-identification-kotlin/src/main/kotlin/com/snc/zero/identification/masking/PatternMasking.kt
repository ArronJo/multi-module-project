package com.snc.zero.identification.masking

object PatternMasking {

    fun id(str: String): String {
        val idPattern = """^(\d{6})[-]?(\d{7})$""".toRegex()
        val matchResult = idPattern.find(str)
        if (matchResult != null) {
            val (birthDate, personalNum) = matchResult.destructured
            return "$birthDate-${personalNum.take(1)}******"
        }
        return str
    }

    fun phoneNum(str: String): String {
        val phonePattern = """^(01[016789])[-]?(\d{3,4})[-]?(\d{4})$""".toRegex()
        val matchResult = phonePattern.find(str)
        if (matchResult != null) {
            val (prefix, _, last) = matchResult.destructured
            return "$prefix-****-$last"
        }
        return str
    }

    fun account(str: String): String {
        val accountPattern = """^(\d{2,6})[-]?(\d{2,6})[-]?(\d{2,6})$""".toRegex()
        val matchResult = accountPattern.find(str)
        if (matchResult != null) {
            val (first, middle, last) = matchResult.destructured
            return "${first.take(2)}${"*".repeat(first.length - 2)}-" +
                "${"*".repeat(middle.length)}-" +
                "${"*".repeat(last.length - 2)}${last.takeLast(2)}"
        }
        return str
    }

    fun card(str: String): String {
        val cardPattern =
            arrayOf(
                """^(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})$""",
                """^(\d{4})[-\s]?(\d{6})[-\s]?(\d{5})$"""
            ).joinToString(separator = "|").trimMargin().toRegex()

        val matchResult = cardPattern.find(str)
        if (matchResult != null) {
            val groups = matchResult.groupValues.drop(1).filter { it.isNotEmpty() }
            return if (groups.size >= 4) {
                "${groups[0]}-****-****-${groups[3]}" // 16자리 카드
            } else {
                "${groups[0]}-******-${groups[2]}" // 15자리 카드 (American Express)
            }
        }
        return str
    }

    fun addressDetail(str: String): String {
        val parts = str.split(" ")

        // 마지막 두 부분을 제외한 나머지를 주소1로, 마지막 두 부분을 주소2로 간주
        val address1 = parts.dropLast(2).joinToString(" ")
        val address2 = parts.takeLast(2).joinToString(" ")

        // 주소2 마스킹
        val maskedAddress2 = maskAddressDetail(address2)

        // 주소1과 마스킹된 주소2를 합침
        return if (address1.isNotBlank()) "$address1 $maskedAddress2" else maskedAddress2
    }

    private fun maskAddressDetail(address2: String): String {
        val parts = address2.split(" ")
        return parts.joinToString(" ") { part ->
            when {
                part.length <= 2 -> part
                part.length <= 5 -> "${part.take(1)}${"*".repeat(part.length - 1)}"
                else -> "${part.take(2)}${"*".repeat(part.length - 2)}"
            }
        }
    }
}
