plugins {
    kotlin("jvm")
}

group = "com.snc.zero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation(project(":library-zero-test-kotlin"))

    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
}