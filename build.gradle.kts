plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.snc.zero"
version = "1.0-beta"

buildscript {
    extra.apply {
        set("jvmTarget", "2.0")
    }
}

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
    }
}

tasks.register<Exec>("runShellScript") {
    println(">>>>> task runShellScript")
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "del_ds_store.bat")
    } else {
        commandLine("sh", "./del_ds_store.sh")
    }
}
tasks.named("compileJava") {
    println(">>>>> task dependsOn(compileJava)")
    dependsOn("runShellScript")
}