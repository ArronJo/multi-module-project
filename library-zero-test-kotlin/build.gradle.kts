import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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
    // https://docs.gradle.org/8.4/userguide/upgrading_version_8.html#test_framework_implementation_dependencies
    implementation(libs.junit.jupiter)
    //implementation(libs.junit.jupiter.api)

    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.testng)
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
        delete("${projectDir}/out")
    }
}
