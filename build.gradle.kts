plugins {
    kotlin("jvm") version "2.0.0"
    //id("java")

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

tasks.test {
    useJUnitPlatform()
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
tasks.register("allProjectDependencies") {
    group = "help"
    description = "Displays dependencies for all projects"

    doLast {
        val targetProp = project.layout.buildDirectory.file("all-dependencies.txt")
        val outputFile = targetProp.get().asFile
        println("outputFile.parentFile: ${outputFile.parentFile}")
        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdirs()
        }
        outputFile.createNewFile()

        outputFile.bufferedWriter().use { writer ->
            subprojects.forEach { subproject ->
                writer.write("\n${subproject.name} dependencies:\n")
                subproject.configurations.forEach { configuration ->
                    writer.write("\n${configuration.name}\n")
                    configuration.dependencies.forEach { dependency ->
                        writer.write("  ${dependency.group}:${dependency.name}:${dependency.version}\n")
                    }
                }
            }
        }
        println("Dependencies written to ${outputFile.absolutePath}")
    }
}

subprojects {
    apply(plugin = "java")

    tasks.register<DependencyReportTask>("allDependencies") {
        // 특정 구성(configuration)의 의존성만 보고 싶다면,
        //configurations = setOf(project.configurations.getByName("compileClasspath"))
    }
    // HTML 보고서를 생성하고 싶다면, 다음과 같이 설정할 수 있습니다:
    tasks.register<HtmlDependencyReportTask>("htmlDependencyReport") {
        reports {
            html.required.set(true)
            html.outputLocation.set(file("${layout.buildDirectory}/reports/dependencies"))
        }
    }

    repositories {
        mavenCentral()
    }

    if (name != "library-zero-test-kotlin") {
        dependencies {
            evaluationDependsOn(":library-zero-test-kotlin")
            dependencies {
                add("implementation", project(":library-zero-test-kotlin"))
            }
            dependencies {
                testImplementation(rootProject.libs.junit.jupiter)
                testRuntimeOnly(rootProject.libs.junit.platform.launcher)
                testRuntimeOnly(rootProject.libs.junit.jupiter.engine)
            }
        }

        tasks.test {
            useJUnitPlatform()
        }
    }
}


///////////////////////////////////////////////////////////
// https://github.com/jacoco/jacoco/releases
jacoco {
    toolVersion = "0.8.12"
}
/*
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
    dependsOn(tasks.test)
}
 */

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