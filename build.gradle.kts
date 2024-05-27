plugins {
    kotlin("jvm") version "2.0.0"

    id("jacoco")
    //id("org.sonarqube") version "3.5.0.2730"
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
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

/*
sonar {
    properties {
        val properties = Properties()
        file("./sonarcloud.properties").inputStream().use { stream ->
            properties.load(stream)
        }

        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.projectKey", properties.getProperty("sonar.token"))
        property("sonar.organization", properties.getProperty("sonar.organization"))
        property("sonar.host.url", properties.getProperty("sonar.host.url"))
    }
}
 */


///////////////////////////////////////////////////////////
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
