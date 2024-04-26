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

    testImplementation(project(":library-zero-test-kotlin"))
    implementation(project(":library-zero-logger-kotlin"))

    implementation("org.jsoup:jsoup:1.16.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
}