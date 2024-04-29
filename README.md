# Multi-module project in Kotlin

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

프로젝트하면서 필요하다고 생각되는 함수들을 모듈로 모아보도록 한다.

- 기능 단위로 module 구성
- 의존성 주입 최소화

---
## Release Note

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
