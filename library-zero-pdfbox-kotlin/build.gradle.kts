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

    implementation("org.apache.pdfbox:pdfbox:2.0.24")
    implementation("org.apache.pdfbox:pdfbox-tools:2.0.24")

    testImplementation(project(":library-zero-test-kotlin"))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
    }
}