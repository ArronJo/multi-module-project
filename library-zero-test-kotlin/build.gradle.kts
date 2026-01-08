plugins {
    kotlin("jvm")
    //id("jacoco")
}

group = "com.snc.zero"
version = "0.1-beta"

repositories {
    mavenCentral()
}

dependencies {
    // https://kotest.io/docs/framework/lifecycle-hooks.html
    // https://kotest.io/docs/framework/testing-styles.html
    // https://test-architect.dev/junit-5-vs-kotest-part-1/
    // https://catsbi.oopy.io/c670a8b6-019d-4640-bdb0-9c80321722d0
    implementation(libs.kotest.runner.junit5)
    implementation(libs.kotest.assertions.core)

    // https://docs.gradle.org/8.4/userguide/upgrading_version_8.html#test_framework_implementation_dependencies
    implementation(libs.junit.jupiter)
    //implementation(libs.junit.jupiter.api)
    // (Optional) If you need "Parameterized Tests"
    //testImplementation(libs.junit.jupiter.params)

    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.testng)
}

kotlin {
    // 상황에 따라 모듈별 JVM 버전을 지정합니다
    //jvmToolchain(rootProject.extra["jvmToolchain"] as Int)

    compilerOptions {
        // Kotlin 언어 버전은 대부분 명시 불필요, 점진적 마이그레이션이 필요한 경우만, "엄격한 버전 제어" 설정 권장:
        //languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
        //apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
    }
}

//tasks.jacocoTestReport {
//    classDirectories.setFrom(
//        files(classDirectories.files.map {
//            fileTree(it) {
//                exclude(
//                    // 특정 파일 제외
//                    //"**/BaseJUnit5Test.kt", "**/BaseJUnit5Test.class",
//                    // 특정 패키지 제외
//                    "com/snc/zero/test/base/**"
//                )
//            }
//        })
//    )
//}

tasks.named("clean") {
    doFirst {
        delete("$projectDir/out")
    }
}

//tasks.named<Test>("test") {
//    enabled = false
//}
