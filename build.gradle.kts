import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm") version "2.0.0"
    //kotlin("jvm") version "1.9.23"
    //id("java")

    // https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/
    id("jacoco")
    id("org.sonarqube") version "5.0.0.4638"
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
//    testImplementation(kotlin("test"))
//    testImplementation(rootProject.libs.junit.jupiter.api)
//    testRuntimeOnly(rootProject.libs.junit.platform.launcher)
//    testRuntimeOnly(rootProject.libs.junit.jupiter.engine)
}

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
    apply(plugin = "jacoco")

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
        finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
        useJUnitPlatform()

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
            }
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

val sonarProperties = Properties()
val sonarPropertiesFile = rootProject.file("sonar.properties")
if (sonarPropertiesFile.exists()) {
    sonarProperties.load(FileInputStream(sonarPropertiesFile))
}

// for GitHub Action
// https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/
sonar {
    properties {
        property("sonar.projectKey", (sonarProperties["projectKey"] as String?) ?: System.getenv("SONAR_PROJECTKEY"))
        property("sonar.organization",  (sonarProperties["organization"] as String?) ?: System.getenv("SONAR_ORGANIZATION"))
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