plugins {
    kotlin("jvm") version "2.0.0"
    id("java")

    // https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/
    id("jacoco")
    id("org.sonarqube") version "5.0.0.4638"
}

group = "com.snc.zero"
version = "1.0-beta"

buildscript {
    extra.apply {
        //set("javaVersion", JavaVersion.VERSION_17)
        set("jvmTarget", "2.0")
    }
}

repositories {
    mavenCentral()
}

dependencies {

}

//java {
//    sourceCompatibility = rootProject.extra["javaVersion"] as JavaVersion
//}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
    }
}


///////////////////////////////////////////////////////////
jacoco {
    toolVersion = "0.8.11"
}

// https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/
sonar {
    properties {
        property("sonar.projectKey", System.getenv("SONAR_PROJECTKEY"))
        property("sonar.organization", System.getenv("SONAR_ORGANIZATION"))
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.exclusions", "**/generated/**, **/test/base/**")
    }
}

System.setProperty("sonar.gradle.skipCompile", "true")

///////////////////////////////////////////////////////////
tasks.register<Exec>("deleteDSStoreShellScript") {
    description = "This is a shell task that deletes the '.DS_Store' file when building a project."
    group = JavaBasePlugin.BUILD_TASK_NAME
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "del_ds_store.bat")
    } else {
        commandLine("sh", "./del_ds_store.sh")
    }
}
tasks.named("compileJava") {
    dependsOn("deleteDSStoreShellScript")
}