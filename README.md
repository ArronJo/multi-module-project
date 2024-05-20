# Multi-module project in Kotlin

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

프로젝트하면서 필요하다고 생각되는 함수들을 모듈로 모아보도록 한다.

- 기능 단위로 module 구성
- 의존성 주입 최소화
- 메소드 체이닝 (Method Chaining) 활용


---
## 확장 함수를 만드는 기준
- 기능 추가: 기존 클래스나 인터페이스에 추가적인 기능이 필요하고, 이 기능이 해당 클래스의 인스턴스와 밀접하게 연관되어 있을 때 확장 함수를 사용하는 것이 좋습니다.
- 유틸리티 함수: 특정 클래스에 대한 유틸리티 기능을 제공하고 싶을 때, 이를 클래스의 일부로 만들기보다는 확장 함수로 구현하여 외부에서 추가하는 형태로 제공할 수 있습니다. 이는 클래스를 깔끔하게 유지하면서 필요한 기능을 확장할 수 있는 방법을 제공합니다.
- 클래스 수정 불가: 외부 라이브러리나 코틀린 표준 라이브러리 같이 소스 코드를 수정할 수 없는 클래스들에 대해 추가적인 기능을 제공하고 싶을 때 확장 함수를 사용합니다.
- 코드 재사용성: 비슷한 기능을 여러 클래스에서 사용해야 할 경우, 공통된 인터페이스나 상위 클래스에 확장 함수를 작성함으로써 코드의 중복을 줄이고 재사용성을 높일 수 있습니다.
- 읽기 쉬운 코드: 메소드 체이닝(method chaining)을 활용하거나, DSL(Domain Specific Language) 형태의 코드를 구현할 때 확장 함수를 사용하면 훨씬 읽기 쉽고 깔끔한 코드를 작성할 수 있습니다.


---
## Release Note


### 2024-05-17

Added new "file-manager" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : FSDirectory, FSFile 클래스 추가
  - class FSDirectory
    - create()
    - delete()
  - class FSFile
    - create()
    - delete()
    - read()
    - write()
    - copy()
    - toBytes()
    - getLength()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : File 확장 함수 추가
  - (String).toFile()
  - (File).mkdirsOrNot()


Added new "identification" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : DeIdentification 클래스 추가
  - class DeIdentification
    - regNo()
    - name()
    - age()
    - address()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Masking 클래스 추가
  - class Masking
    - regNo()
    - name()
    - phoneNum()
    - email()
    - masking()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Faker 클래스 추가
  - class Faker
    - object Name
      - fakeKoreanName()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : MoneyFormat 클래스 추가
  - (Long).formatKoreanMoney()
  - (Long).formatRealKoreanMoney()


### 2024-05-14

Updated "core" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : DateFormat 확장 함수 추가
  - (String).formatDateTime()
  - (Calendar).formatDateTime()
  - (Date).formatDateTime()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : FileSizeFormat 확장 함수 추가
  - (Long).formatFileSize()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Calendar 확장 함수 추가
  - (Calendar).setYear(), addYear(), getYear()
  - (Calendar).setMonth(), addMonth(), getMonth()
  - (Calendar).setDay(), addDay(), getDay()
  - (Calendar).setHour(), addHour(), getHour()
  - (Calendar).setMinute(), addMinute(), getMinute()
  - (Calendar).setSecond(), addSecond(), getSecond()
  - (Calendar).setMillisecond(), addMillisecond(), getMillisecond()
  - (Calendar).startOfMonth(), endOfMonth()
  - (Calendar).startOfDay(), endOfDay()
  - (Calendar).getLastDayOfMonth()
  - (Calendar).diff()
  - (String).toCalendar()
  - (Date).toCalendar()


- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : JSON 확장 함수 추가
  - (Any).toJsonString()
  - (String).toObject()


### 2024-05-10

Updated "core" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Filter, QueryString 확장 함수 추가
  - (String).filter()
  - (Map<*, *>).toQueryString()
  - (String).getQueryStringValue()
  - (String).queryStringToMap()


- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Random 클로벌 확장 함수, Draw 확장 함수 추가
  - randomInt()
  - ranomgString()
  - (List<T>).getRandomItem()
  - (MutableMap<T, Double>).getWeightedRandom()


- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Counter 테스트 클래스 추가


### 2024-05-08

Added new "file-manager" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : FSInfo 클래스 추가
  - class FSInfo
    - getReadableFileSize()

Updated "crypto" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : 해시 신규 클래스 추가
  - class Hash
    - object SHA-2
    - object SHA-3
    - object SHAKE

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Cipher Extension 함수 추가
  - (String).encrypt()
  - (String).decrypt()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Encoder,Decoder Extension 함수 추가
  - (String).encodeBase64()
  - (ByteArray).encodeBase64()
  - (String).decodeBase64()

- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Hash Extension 함수 추가
  - (String).toHmacSHA224()
  - (String).toHmacSHA256()
  - (String).toHmacSHA384()
  - (String).toHmacSHA512()
  - (String).toSha224()
  - (String).toSha256()
  - (String).toSha384()
  - (String).toSha512()
  - (String).toShake128()
  - (String).toShake256()


### 2024-05-03

Added new "crypto" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : 암호화 신규 클래스 추가
  - class Cipher
    - object AES
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : 인코딩 신규 클래스 추가
  - class Encoder, Decoder
    - object Base64 (w/Safe Url)


### 2024-04-26

build.gradle.kts 정리
- ![chore](https://img.shields.io/badge/Chore-blue.svg): jvmToolchain 버전 관리 변수 설정
    ```
    buildscript {
        extra.apply {
            set("jvmToolchainVersion", 17)
        }
    }

    kotlin {
        jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
    }
    ```
- ![chore](https://img.shields.io/badge/Chore-blue.svg): test framework 지정
    ```
    # The automatic loading of test framework implementation dependencies has been deprecated.
    # This is scheduled to be removed in Gradle 9.0.
    # Declare the desired test framework directly on the test suite or explicitly declare the test framework implementation dependencies on the test's runtime classpath.
    # Consult the upgrading guide for further information:
    #    https://docs.gradle.org/8.4/userguide/upgrading_version_8.html#test_framework_implementation_dependencies

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    ```


Added new "validation" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : 문자열 검수 관련 신규 함수 추가
  - (String).isLetter()
  - (String).isUpperCase()
  - (String).isLowerCase()
  - (String).isNumber()
  - (String).isHangul()
  - (String).validate()
  - (String).validatePassword()


### 2024-04-25

Updated "core" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Byte, HexString 신규 함수 추가
  - (String, ByteArray, Byte).toHexString()
  - (String).toBytes()


Added new "xss" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : XSS, Escape 신규 함수 추가
  - (String).cleanXSS()
  - (String).escapeHtmlEntities()
  - (String).unescapeHtmlEntities()


### 2024-04-23


Updated "core" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : 신규 확장 함수 추가
  - (Int, Long).padStart()
  - (Int, Long).padEnd()

Added new "logger" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : Wrapping the Logging Module
  - TLogger.kt 추가
  - Deprecated API 미구현

Added new "test" module.
- ![feat](https://img.shields.io/badge/Feature-%2300c806.svg) : JUnit 테스트 용 부모 클래스 추가
  - BaseTest.kt 추가


### 2024-04-21
문서 최초 생성
- ![new](https://img.shields.io/badge/New-blue.svg) kotlin 확장 함수를 기반으로 하는 라이브러리를 만들어보자. 
