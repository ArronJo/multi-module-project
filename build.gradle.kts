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

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(rootProject.extra["jvmToolchainVersion"] as Int)
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