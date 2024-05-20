plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.snc.zero"
version = "1.0-beta"

buildscript {
    extra.apply {
        set("jvmToolchainVersion", 17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
}
