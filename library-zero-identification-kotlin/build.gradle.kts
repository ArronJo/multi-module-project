plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.snc.zero"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library-zero-kotlin"))
    //testImplementation(project(":library-zero-kotlin"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}