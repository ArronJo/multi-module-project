package com.snc.zero.personalinfo

/**
 * PersonalInfoRegex 객체 내에 다양한 개인정보 항목에 대한 정규식을 정의하고 있습니다.
 *
 * 각 정규식의 용도는 다음과 같습니다:
 *  - residentRegistrationNumber: 주민등록번호
 *  - foreignerRegistrationNumber: 외국인등록번호
 *  - driverLicenseNumber: 운전면허번호
 *  - passportNumber: 여권번호
 *  - mobilePhoneNumber: 휴대폰 번호
 *  - emailAddress: 이메일 주소
 *  - creditCardNumber: 신용카드 번호
 *  - bankAccountNumber: 은행 계좌번호 (일반적인 형식)
 *  - ipAddress: IP 주소
 */
object PersonalInfoRegex {
    // 주민등록번호
    val residentRegistrationNumber = Regex("""^\d{6}-[1-4]\d{6}$""")

    // 외국인등록번호
    val foreignerRegistrationNumber = Regex("""^\d{6}-[5-8]\d{6}$""")

    // 운전면허번호
    val driverLicenseNumber = Regex("""^\d{2}-\d{2}-\d{6}-\d{2}$""")

    // 여권번호
    val passportNumber = Regex("""^[A-Z]{1}\d{8}$|^[A-Z]{1}\d{3}[A-Z]{1}\d{4}$""")

    // 휴대폰 번호
    val mobilePhoneNumber = Regex("""^01[016789]-?\d{3,4}-?\d{4}$""")

    // 이메일 주소
    val emailAddress = Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$""")

    // 신용카드 번호
    val creditCardNumber = Regex("""^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6011[0-9]{12}|622((12[6-9]|1[3-9][0-9])|([2-8][0-9][0-9])|(9(([0-1][0-9])|(2[0-5]))))[0-9]{10}|64[4-9][0-9]{13}|65[0-9]{14}|3(?:0[0-5]|[68][0-9])[0-9]{11}|3[47][0-9]{13})$""")

    // 계좌번호 (일반적인 형식, 은행별로 다를 수 있음)
    val bankAccountNumber = Regex("""^\d{11,14}$""")

    // IP 주소
    val ipAddress = Regex("""^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""")
}
