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

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
    }
}


///////////////////////////////////////////////////////////
jacoco {
    toolVersion = "0.8.11"

    println()
    println("LOG: jacoco.layout.buildDirectory: ${layout.buildDirectory}")
    println()
    println("LOG: jacoco.reportsDirectory: ${reportsDirectory.get()}")
    //reportsDirectory.set(layout.buildDirectory.dir("jacoco").get())
    //reportsDirectory.set(layout.buildDirectory.dir("jacocoTestReport"))
    //println()
    //println("jacoco.reportsDirectory: ${reportsDirectory.get()}")
}
tasks.test {
    println()
    println("LOG: tasks.test { }")

    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    println()
    println("LOG: jacoco.report.xml: ${reports.xml.required.get()}")
    println("LOG: jacoco.report.html: ${reports.html.required.get()}")
    println("LOG: jacoco.report.csv: ${reports.csv.required.get()}")

    dependsOn(tasks.test) // tests are required to run before generating the report
}

//sonar {
//    properties {
//        println()
//        println("sonar.coverage.jacoco.xmlReportPaths: ${property("sonar.coverage.jacoco.xmlReportPaths")}")
//
//        property("sonar.projectKey", System.getenv("SONAR_PROJECTKEY"))
//        property("sonar.organization", System.getenv("SONAR_ORGANIZATION"))
//        property("sonar.host.url", "https://sonarcloud.io")
//        //property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory}/reports/tests/test/index.html")
//        property("sonar.coverage.jacoco.xmlReportPaths", "target/site/jacoco/jacoco.xml")
//    }
//}


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