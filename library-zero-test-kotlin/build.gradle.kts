plugins {
    kotlin("jvm")
}

group = "com.snc.zero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library-zero-logger-kotlin"))

    implementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.testng:testng:7.9.0")
}

tasks.test {
    useJUnitPlatform()
}
/*
tasks.test {
    useTestNG()
}
 */
kotlin {
    jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
}