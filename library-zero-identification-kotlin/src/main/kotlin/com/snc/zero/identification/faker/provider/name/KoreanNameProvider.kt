package com.snc.zero.identification.faker.provider.name

class KoreanNameProvider : NameProvider() {

    // 이름, 성 순위
    // https://en.wikipedia.org/wiki/List_of_Korean_surnames
    // https://forebears.io/south-korea/surnames
    // https://forebears.io/south-korea/forenames
    init {
        firstNamesFemale.addAll(
            arrayOf(
                "경숙", "경자", "경희",
                "명숙", "명자", "미경", "미숙", "미영", "미정", "민서", "민지",
                "보람",
                "서연", "서영", "서우", "서윤", "서현", "선영", "수민", "수빈", "수진", "숙자", "순옥",
                "순자",
                "아름", "영미", "영숙", "영순", "영자", "영희", "예원", "예은", "예지", "예진", "옥순",
                "옥자", "유진", "윤서", "은경", "은서", "은영", "은정", "은주", "은지",
                "정숙", "정순", "정자", "정희", "지민", "지아", "지안", "지연", "지영", "지우", "지원",
                "지은", "지현", "지혜",
                "채원", "춘자",
                "하윤", "하은", "현숙", "현정", "현주", "현지", "혜진"
            )
        )

        firstNamesMale.addAll(
            arrayOf(
                "건우", "경수", "광수",
                "도윤", "도현", "동현",
                "민석", "민수", "민재", "민준",
                "병철",
                "상철", "상현", "상호", "상훈", "서준", "성민", "성수", "성진", "성현", "성호", "성훈",
                "승민", "승현", "시우",
                "영길", "영수", "영식", "영일", "영진", "영철", "영호", "영환", "예준", "우진",
                "재현", "재호", "정남", "정수", "정식", "정우", "정웅", "정호", "정훈", "종수", "주원", "준서",
                "준영", "준혁", "준호", "중수", "지후", "지훈", "진우", "진호",
                "현우", "현준"
            )
        )

        lastNames["김"] = 0.10689
        lastNames["이"] = 0.07307
        lastNames["박"] = 0.04192
        lastNames["정"] = 0.02333
        lastNames["최"] = 0.02151
        lastNames["조"] = 0.01176
        lastNames["강"] = 0.01055
        lastNames["윤"] = 0.01020
        lastNames["장"] = 0.00992
        lastNames["임"] = 0.00823
        lastNames["한"] = 0.00773
        lastNames["오"] = 0.00763
        lastNames["서"] = 0.00751
        lastNames["신"] = 0.00741
        lastNames["권"] = 0.00705
        lastNames["황"] = 0.00697
        lastNames["안"] = 0.00685
        lastNames["송"] = 0.00683
        lastNames["류"] = 0.00642
        lastNames["전"] = 0.00559
        lastNames["홍"] = 0.00558
        lastNames["고"] = 0.00471
        lastNames["문"] = 0.00464
        lastNames["양"] = 0.00460
        lastNames["손"] = 0.00457
        lastNames["배"] = 0.00400
        lastNames["조"] = 0.00398
        lastNames["백"] = 0.00381
        lastNames["허"] = 0.00326
        lastNames["유"] = 0.00302
        lastNames["남"] = 0.00275
        lastNames["심"] = 0.00271
        lastNames["노"] = 0.00256
        lastNames["정"] = 0.00243
        lastNames["하"] = 0.00230
        lastNames["곽"] = 0.00203
        lastNames["성"] = 0.00199
        lastNames["차"] = 0.00194
        lastNames["주"] = 0.00194
        lastNames["우"] = 0.00194
        lastNames["구"] = 0.00193
        lastNames["신"] = 0.00192
        lastNames["임"] = 0.00191
        lastNames["나"] = 0.00186
        lastNames["전"] = 0.00186
        lastNames["민"] = 0.00171
        lastNames["유"] = 0.00167
        lastNames["진"] = 0.00159
        lastNames["지"] = 0.00153
        lastNames["엄"] = 0.00144
    }
}
