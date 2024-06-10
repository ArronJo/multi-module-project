package com.snc.zero.identification.deidentification2

import com.snc.zero.identification.faker.Faker

object DeIdentification {

    object Pseudonymization {

        fun name(firstName: String, lastName: String, provider: Faker.ProviderType = Faker.ProviderType.KOREAN, female: Boolean = false): Array<String> {
            var v: Array<String>
            while (true) {
                v = Faker.Name.fake(provider, female)
                if (!v[0].lowercase().startsWith(firstName.substring(0, 1).lowercase())
                    && !v[1].lowercase().startsWith(lastName.substring(0, 1).lowercase())
                ) {
                    break
                }
            }
            return v
        }

        fun age(v: Int): String {
            val age = bornInThose(v).toString()
            return "$age 대"
        }
    }

    object DataReduction {

        fun regNo(v: String): String {
            val sex = when (v.replace("[^0-9]".toRegex(), "")[6]) {
                '1', '3', '5', '7', '9' -> "남자"
                else -> "여자"
            }
            return "${birth(v.substring(0, 6))} $sex"
        }

        fun birth(v: String): String {
            val yyyy = if (v.length == 8) {
                4
            } else if (v.length == 6) {
                2
            } else {
                0
            }
            return "${bornInThose(v.substring(0, yyyy))}년대생"
        }
    }

    object DataSuppression {

        fun name(v: String): String {
            return v.substring(0, 1) + "씨"
        }

        fun age(v: Int): String {
            val a = v / 10
            return "${a * 10}~${(a + 1) * 10}세"
        }

        fun address(addr: String): String {
            val region = arrayOf(
                "서울", "부산", "대구", "인천", "광주",
                "대전", "울산", "세종", "경기도", "강원",
                "충청북도", "충청남도", "전북", "전라북도", "전라남도",
                "경상북도", "경상남도", "제주"
            )
            for (r in region) {
                if (addr.startsWith(r)) {
                    return "$r 거주"
                }
            }
            return "거주지 미확인"
        }
    }

    object DataMasking {

        fun name(v: String): String {
            return v.substring(0, 1) + "◯".repeat(2)
        }
    }

    private fun bornInThose(age: Int): Int {
        return age / 10 * 10
    }

    private fun bornInThose(age: String): Int {
        return bornInThose(Integer.parseInt(age))
    }
}