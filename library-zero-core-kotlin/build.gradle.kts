import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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
    implementation(libs.google.gson)

    testImplementation(project(":library-zero-test-kotlin"))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)
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

tasks.named("clean") {
    doFirst {
        delete("${projectDir}/out")
    }
}
