import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm") version "2.0.0" // "1.9.23"
    // id("java")

    // checck latest version
    // https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/
    id("jacoco")
    alias(libs.plugins.sonarqube) // id("org.sonarqube") version "5.1.0.4882"

    // https://wiki.owasp.org/images/b/bd/OWASP_Top_10-2017-ko.pdf
    // https://rcan.net/149?category=998453
    // https://github.com/dependency-check/dependency-check-sonar-plugin
    // Dependency-check OWASP
    alias(libs.plugins.dependencycheck) // id("org.owasp.dependencycheck") version "8.0.2"

    // https://github.com/JLLeitschuh/ktlint-gradle
    // https://plugins.gradle.org/plugin/org.jlleitschuh.gradle.ktlint
    // https://pinterest.github.io/ktlint/0.49.1/rules/experimental/#multiline-expression-wrapping
    //alias(libs.plugins.ktlint) // id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "com.snc.zero"
version = "1.0-beta"

buildscript {
    extra.apply {
        set("javaVersion", JavaVersion.VERSION_18)
        set("jvmTarget", "2.0")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //implementation("org.owasp:dependency-check-gradle:5.3.0")

//    testImplementation(kotlin("test"))
//    testImplementation(rootProject.libs.junit.jupiter.api)
//    testRuntimeOnly(rootProject.libs.junit.platform.launcher)
//    testRuntimeOnly(rootProject.libs.junit.jupiter.engine)
}

//dependencyCheck {
//    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
//}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = rootProject.extra["javaVersion"] as JavaVersion
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["jvmTarget"] as String))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

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

//sourceSets {
//    getByName("main") {
//        java.srcDirs("src/main/java", "src/main/kotlin")
//        resources.srcDirs("src/main/res")
//    }
//    getByName("test") {
//        java.srcDirs("src/test/java", "src/test/kotlin")
//        resources.srcDirs("src/test/res")
//    }
//}

sourceSets {
    val main by getting {
        java {
            setSrcDirs(listOf("src/main/java"))
        }
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
    val test by getting {
        java {
            setSrcDirs(listOf("src/test/java"))
        }
        resources {
            setSrcDirs(listOf("src/test/resources"))
        }
    }
}

///////////////////////////////////////////////////////////
// Jacoco
//
// -Getting Started: https://docs.gradle.org/current/userguide/jacoco_plugin.html
//                   https://yeoonjae.tistory.com/entry/Project-Gradle-Build-%EC%8B%9C-JaCoCo-%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0
// -Release: https://github.com/jacoco/jacoco/releases
jacoco {
    toolVersion = "0.8.12"
    //reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}
/*
tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    dependsOn(tasks.test)
}
 */

///////////////////////////////////////////////////////////
// SonarQube
val sonarProperties = Properties()
val sonarPropertiesFile = rootProject.file("sonar-project.properties")
if (sonarPropertiesFile.exists()) {
    sonarProperties.load(FileInputStream(sonarPropertiesFile))
}
//println("sonarProperties: $sonarProperties")

// for GitHub Action
// https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/
// Task 'sonarqube' is deprecated. Use 'sonar' instead.
sonar {
    properties {
        val sonarProjectKey = (sonarProperties["sonar.projectKey"] as String?) ?: System.getenv("SONAR_PROJECTKEY")
        val sonarOrganization = (sonarProperties["sonar.organization"] as String?) ?: System.getenv("SONAR_ORGANIZATION")
        val sonarHost = (sonarProperties["sonar.host.url"] as String?) ?: "https://sonarcloud.io"
        val sonarToken = (sonarProperties["sonar.token"] as String?) ?: System.getenv("SONAR_TOKEN")

        //println("-sonarProjectKey: $sonarProjectKey")
        //println("-sonarOrganization: $sonarOrganization")
        //println("-sonarToken: $sonarToken")

        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.projectKey", sonarProjectKey)
        property("sonar.organization", sonarOrganization)
        property("sonar.token", sonarToken)
        //property("sonar.login", sonarToken)   // The property 'sonar.login' is deprecated and will be removed in the future. Please use the 'sonar.token' property instead when passing a token.
        property("sonar.host.url", sonarHost)

        property("sonar.java.binaries", "${layout.buildDirectory}/classes")

        property("sonar.coverage.exclusions", "**/generated/**, **/test/base/**")

        // Sonarqube와 통합하려면 다음과 같이 설정할 수 있습니다:
        //property("sonar.dependencyCheck.jsonReportPath", "build/reports/dependency-check-report.json")
        //property("sonar.dependencyCheck.htmlReportPath", "build/reports/dependency-check-report.html")
    }
}

System.setProperty("sonar.gradle.skipCompile", "true")

///////////////////////////////////////////////////////////
// Shell
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

///////////////////////////////////////////////////////////
// Sub-Projects Settings
subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "org.owasp.dependencycheck")
    //apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    dependencies {
        if (name != "library-zero-test-kotlin") {
            evaluationDependsOn(":library-zero-test-kotlin")
            dependencies {
                add("implementation", project(":library-zero-test-kotlin"))
            }
        }
        dependencies {
            testImplementation(rootProject.libs.junit.jupiter)
            testRuntimeOnly(rootProject.libs.junit.platform.launcher)
            testRuntimeOnly(rootProject.libs.junit.jupiter.engine)
        }
    }

    // Optionally configure plugin
    //configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    //    debug.set(true)
    //}

    tasks.register<DependencyReportTask>("allDependencies") {
        description = "Inject dependencies into subprojects."
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        // 특정 구성(configuration)의 의존성만 보고 싶다면,
        //configurations = setOf(project.configurations.getByName("compileClasspath"))
    }
    // HTML 보고서를 생성하고 싶다면, 다음과 같이 설정할 수 있습니다:
    tasks.register<HtmlDependencyReportTask>("htmlDependencyReport") {
        description = "Generates the HTML documentation for this project."
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        reports {
            html.required.set(true)
            html.outputLocation.set(file("${layout.buildDirectory}/reports/dependencies"))
        }
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.test {
        // 실행순서 : test -> jacocoTestReport -> jacocoTestCoverageVerification
        finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)

        jacoco {
            //excludes.add("com/snc/test/test/**")
            //exclude("**/BaseJUnit5Test*.class")
        }
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
            //html.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco"))
        }

        //additionalClassDirs(files("build/classes/kotlin/main"))
        //additionalSourceDirs(files("src/main/kotlin"))
        //executionData(files("build/jacoco/test.exec"))

        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("com/snc/zero/test/base/**")
                //exclude("**/BaseJUnit5Test*.class")
            },
        )
    }

    tasks.jacocoTestCoverageVerification {
        /*
        violationRules {
            rule {
                enabled = true
                element = "CLASS"

                // 적용 할 패키지(기본은 전체)
                // includes = []

                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = 0.50.toBigDecimal()
                }

                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = 0.50.toBigDecimal()
                }

                limit {
                    counter = "LINE"
                    value = "TOTALCOUNT"
                    maximum = 200.toBigDecimal()
                }
            }
        }
         */
    }
}
