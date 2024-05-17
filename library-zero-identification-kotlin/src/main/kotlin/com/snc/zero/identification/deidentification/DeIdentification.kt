package com.snc.zero.identification.deidentification

class DeIdentification {

    companion object {

        @JvmStatic
        fun regNo(v: String): String {
            val sex = when (v.replace("[^0-9]".toRegex(), "")[6]) {
                '1', '3', '5', '7', '9' -> "남자"
                '2', '4', '6', '8', '0' -> "여자"
                else -> "모름"
            }
            return bornInThose(v.substring(0, 2)).toString() + "년대생 " + sex
        }

        @JvmStatic
        fun name(v: String): String {
            return v.substring(0, 1) + "씨"
        }

        @JvmStatic
        fun age(v: Int): String {
            return bornInThose(v).toString() + "대"
        }

        @JvmStatic
        private fun bornInThose(age: Int): Int {
            return age / 10 * 10
        }
        @JvmStatic
        private fun bornInThose(age: String): Int {
            return bornInThose(Integer.parseInt(age))
        }

        @JvmStatic
        fun address(addr: String): String {
            val region = arrayOf(
                "서울",
                "부산",
                "대구",
                "인천",
                "광주",
                "대전",
                "울산",
                "세종",
                "경기도",
                "강원",
                "충청북도",
                "충청남도",
                "전북",
                "전라남도",
                "경상북도",
                "경상남도",
                "제주"
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