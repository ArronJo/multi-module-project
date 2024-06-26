package com.snc.zero.identification.masking

object PatternMasking {

    fun id(str: String): String {
        val idPattern = """^(\d{6})[-]?(\d{7})$""".toRegex()

        return when {
            idPattern.matches(str) -> {
                val matchResult = idPattern.find(str)
                if (matchResult != null) {
                    val (birthDate, personalNum) = matchResult.destructured
                    "$birthDate-${personalNum.take(1)}******"
                } else {
                    str
                }
            }

            else -> str
        }
    }

    fun phoneNum(str: String): String {
        val phonePattern = """^(01[016789])[-]?(\d{3,4})[-]?(\d{4})$""".toRegex()

        return when {
            phonePattern.matches(str) -> {
                val matchResult = phonePattern.find(str)
                if (matchResult != null) {
                    val (prefix, _, last) = matchResult.destructured
                    "$prefix-****-$last"
                } else {
                    str
                }
            }

            else -> str
        }
    }

    fun account(str: String): String {
        val accountPattern = """^(\d{2,6})[-]?(\d{2,6})[-]?(\d{2,6})$""".toRegex()

        return when {
            accountPattern.matches(str) -> {
                val matchResult = accountPattern.find(str)
                if (matchResult != null) {
                    val (first, middle, last) = matchResult.destructured
                    "${first.take(2)}${"*".repeat(first.length - 2)}-" +
                            "${"*".repeat(middle.length)}-" +
                            "${"*".repeat(last.length - 2)}${last.takeLast(2)}"
                } else {
                    str
                }
            }

            else -> str
        }
    }

    fun card(str: String): String {
        val cardPattern =
            """^(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})[-\s]?(\d{4})$|^(\d{4})[-\s]?(\d{6})[-\s]?(\d{5})$""".toRegex()

        return when {
            cardPattern.matches(str) -> {
                val matchResult = cardPattern.find(str)
                if (matchResult != null) {
                    val groups = matchResult.groupValues.drop(1).filter { it.isNotEmpty() }
                    when (groups.size) {
                        4 -> "${groups[0]}-****-****-${groups[3]}"  // 16자리 카드
                        3 -> "${groups[0]}-******-${groups[2]}"     // 15자리 카드 (American Express)
                        else -> str
                    }
                } else {
                    str
                }
            }

            else -> str
        }
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