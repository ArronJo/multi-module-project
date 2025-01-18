import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
}

group = "com.snc.zero"
version = "0.1-beta"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.logback.core)
    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging.jvm)
}

//java {
//    sourceCompatibility = rootProject.extra["javaVersion"] as JavaVersion
//}

kotlin {
    compilerOptions {
        languageVersion.set(KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
        apiVersion.set(KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
    }
}

// 직접적인 의존성이 아닌 취약점이라면, dependencyResolutionManagement를 사용하여 강제로 버전을 올릴 수 있습니다:
// Server-side Request Forgery (SSRF) [Low Severity][https://security.snyk.io/vuln/SNYK-JAVA-CHQOSLOGBACK-8539865]
// in ch.qos.logback:logback-core@1.3.14
//    introduced by com.pinterest.ktlint:ktlint-cli@1.5.0
//      > ch.qos.logback:logback-classic@1.3.14
//      > ch.qos.logback:logback-core@1.3.14
//configurations.all {
//    resolutionStrategy.eachDependency {
//        if (requested.group == "ch.qos.logback" && requested.name == "logback-classic") {
//            useVersion("1.5.13")
//            because("Fix for CVE vulnerability")
//        }
//    }
//}

// logback 버전 강제화 (force):
configurations.all {
    resolutionStrategy.force(
        "ch.qos.logback:logback-core:1.5.0",
        "ch.qos.logback:logback-classic:1.5.0"
    )
}

tasks.named("clean") {
    doFirst {
        delete("$projectDir/out")
    }
}

//tasks.named<Test>("test") {
//    enabled = false
//}
