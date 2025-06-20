package com.snc.zero.identification.deidentification2

import com.snc.zero.identification.faker.Faker

/**
 * 개인정보 비식별 조치 (De-identification)
 * - 익명화 (Anonymization) : 개인식별정보 삭제 혹은 알아볼수 없게 변환
 * - 가명화 (Pseudonymization)
 *
 * 처리기법
 * - 가명처리 (Pseudonymization)
 * - 총계처리 (Aggregation)
 * - 데이터 삭제 (Data Reduction)
 * - 데이터 범주화 (Data Suppression)
 * - 데이터 마스킹 (Data Masking)
 */
object DeIdentification {

    /**
     * 가명처리 (Pseudonymization)
     * : 예시) 홍길동, 35세, 서울 거주, 한국대 재학  ->  임꺽정, 30대, 서울 거주, 국제대 재학 (from 행안부, 2016)
     * : 세부기술: 1)휴리스틱 가명화, 2)암호화, 3)교환방법
     */
    object Pseudonymization {

        fun name(firstName: String, lastName: String, provider: Faker.ProviderType = Faker.ProviderType.KOREAN, female: Boolean = false): Array<String> {
            var v: Array<String>
            while (true) {
                v = Faker.Name.fake(provider, female)
                if (!v[0].lowercase().startsWith(firstName.substring(0, 1).lowercase()) &&
                    !v[1].lowercase().startsWith(lastName.substring(0, 1).lowercase())
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

    /**
     * 데이터 삭제 (Data Reduction)
     * : 예시) 주민등록번호 901206-1234567  ->  90년대 생, 남자 (from 행안부, 2016)
     * : 세부기술: 8)식별자 삭제 9)식별자 부분삭제 10)레코드 삭제 11)식별요소 전부삭제
     */
    object DataReduction {

        fun regNo(v: String): String {
            val sex = when (v.replace("[^0-9]".toRegex(), "")[6]) {
                '1', '3', '5', '7', '9' -> "남자"
                else -> "여자"
            }
            return "${birth(v.substring(0, 6))} $sex"
        }

        fun birth(v: String): String {
            val pos = when (v.length) {
                8 -> { 4 }
                6 -> { 2 }
                else -> { 0 }
            }
            return "${bornInThose(v.substring(0, pos))}년대생"
        }
    }

    /**
     * 데이터 범주화 (Data Suppression)
     * : 예시) 홍길동, 35세  ->  홍씨, 30~40세 (from 행안부, 2016)
     * : 예시) 서울특별시 송파구 가락본동 78번지  →  서울시 송파구  →  서울 (from 행안부, 2016)
     * : 세부기술: 12)감추기 13)랜덤라운딩 14)범위 방법 15)제어 라운딩
     */
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
                "서울", //"서울특별시",
                "부산", //"부산광역시",
                "대구", //"대구광역시",
                "인천", //"인천광역시",
                "광주", //"광주광역시",
                "대전", //"대전광역시",
                "울산", //"울산광역시",
                "세종", //"세종특별자치시",
                "경기도",
                "강원", //"강원특별자치도",
                "충청북도",
                "충청남도",
                "전북", //"전북특별자치도",
                "전라북도",
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

    /**
     * 데이터 마스킹 (Data Masking)
     * : 예시) 홍길동, 35세, 서울 거주, 한국대 재학  ->  홍ㅇㅇ, 35세, 서울 거주, ㅇㅇ대학 재학 (from 행안부, 2016)
     * : 세부기술: 16)임의 잡음 추가 17)공백과 대체
     */
    object DataMasking {

        fun name(v: String): String {
            // 이름이 외자 인 경우 등이 존재하므로 통일성을 가지기 위해 2개로 고정한다. (v.length - 1) → 2 로 수정
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
