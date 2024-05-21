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
    implementation(project(":library-zero-logger-kotlin"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
}