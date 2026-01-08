plugins {
    kotlin("jvm")
    id("jacoco")
}

group = "com.snc.zero"
version = "0.1-beta"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library-zero-logger-kotlin"))
    implementation(libs.cache.caffeine)

    testImplementation(project(":library-zero-test-kotlin"))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)
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

tasks.named("clean") {
    doFirst {
        delete("$projectDir/out")
    }
}

//tasks.named<Test>("test") {
//    enabled = false
//}
