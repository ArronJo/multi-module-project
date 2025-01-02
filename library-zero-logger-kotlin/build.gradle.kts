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
