plugins {
    kotlin("jvm")
}

group = "com.snc.zero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))

    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
}