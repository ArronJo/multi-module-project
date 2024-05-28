plugins {
    kotlin("jvm") version "2.0.0"

    id("jacoco")
    id("org.sonarqube") version "4.4.1.3373"
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


///////////////////////////////////////////////////////////
tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}

sonar {
    properties {
        property("sonar.projectKey", System.getenv("SONAR_PROJECTKEY"))
        property("sonar.organization", System.getenv("SONAR_ORGANIZATION"))
        property("sonar.host.url", "https://sonarcloud.io")
    }
}


///////////////////////////////////////////////////////////
tasks.register<Exec>("runShellScript") {
    description = "This is a shell task that deletes the '.DS_Store' file when building a project."
    group = JavaBasePlugin.BUILD_TASK_NAME
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "del_ds_store.bat")
    } else {
        commandLine("sh", "./del_ds_store.sh")
    }
}
tasks.named("compileJava") {
    dependsOn("runShellScript")
}