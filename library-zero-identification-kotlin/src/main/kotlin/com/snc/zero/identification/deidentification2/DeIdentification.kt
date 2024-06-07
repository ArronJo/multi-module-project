package com.snc.zero.identification.deidentification2

import com.snc.zero.identification.faker.Faker

class DeIdentification {

    class Pseudonymization {

        companion object {

            @JvmStatic
            fun name(name: String, female: Boolean = false): String {
                var v: Array<String>
                do {
                    v = Faker.Name.fake(Faker.ProviderType.KOREAN, female)
                    if (name.substring(0, 1) != v[0]
                        && name.substring(1) != v[1]) {
                        break
                    }
                } while (true)
                return v.joinToString(separator = "")
            }

            @JvmStatic
            fun age(v: Int): String {
                val age = bornInThose(v).toString()
                return "$age 대"
            }
        }
    }

    class DataReduction {

        companion object {

            @JvmStatic
            fun regNo(v: String): String {
                val sex = when (v.replace("[^0-9]".toRegex(), "")[6]) {
                    '1', '3', '5', '7', '9' -> "남자"
                    else -> "여자"
                }
                return "${birth(v.substring(0, 6))} $sex"
            }

            @JvmStatic
            fun birth(v: String): String {
                val yyyy = if (v.length == 8) { 4 } else if (v.length == 6) { 2 } else { 0 }
                return "${bornInThose(v.substring(0, yyyy))}년대생"
            }
        }
    }

    class DataSuppression {

        companion object {

            @JvmStatic
            fun name(v: String): String {
                return v.substring(0, 1) + "씨"
            }

            @JvmStatic
            fun age(v: Int): String {
                val a = v / 10
                return "${a*10}~${(a+1)*10}세"
            }

            @JvmStatic
            fun address(addr: String): String {
                val region = arrayOf(
                    "서울", "부산", "대구", "인천", "광주",
                    "대전", "울산", "세종", "경기도", "강원",
                    "충청북도", "충청남도", "전라북도", "전라남도",
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
    }

    class DataMasking {

        companion object {

            @JvmStatic
            fun name(v: String): String {
                // 이름이 외자 인 경우 등이 존재하므로 통일성을 가지기 위해 2개로 고정한다. (v.length - 1) → 2 로 수정
                return v.substring(0, 1) + "◯".repeat(2)
            }
        }
    }

    companion object {

        @JvmStatic
        private fun bornInThose(age: Int): Int {
            return age / 10 * 10
        }

        @JvmStatic
        private fun bornInThose(age: String): Int {
            return bornInThose(Integer.parseInt(age))
        }
    }
}