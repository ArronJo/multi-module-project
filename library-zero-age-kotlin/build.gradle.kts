plugins {
    kotlin("jvm")
}

group = "com.snc.zero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(project(":library-zero-test-kotlin"))
    implementation(project(":library-zero-logger-kotlin"))

    implementation(project(":library-zero-core-kotlin"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}