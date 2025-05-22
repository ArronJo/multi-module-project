//import org.gradle.api.artifacts.verification.DependencyVerificationMode as VerificationMode
import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //`kotlin-dsl`
    //id("java")
    kotlin("jvm") version "2.1.0" // id("org.jetbrains.kotlin.jvm") version "2.1.0" "2.0.22" "1.9.23"
    kotlin("plugin.serialization") version "1.8.0"

    // Check latest version
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
    alias(libs.plugins.ktlint) // id("org.jlleitschuh.gradle.ktlint") version "12.0.3"

    // 규칙 충돌 관리를 위해 detekt 는 off 하도록 한다.
    // https://github.com/detekt/detekt
    //alias(libs.plugins.detekt) // id("io.gitlab.arturbosch.detekt") version "1.23.7"

    // SBOM 생성
    // https://plugins.gradle.org/plugin/org.cyclonedx.bom
    // 명령어: `./gradlew cyclonedxBom`
    // 취약점검사: `trivy sbom ./build/reports/bom.json`
    // https://github.com/CycloneDX/cyclonedx-gradle-plugin
    // https://scribesecurity.com/ko/sbom/sample-sbom/#sbom-samples
    // https://mvnrepository.com/artifact/org.cyclonedx.bom/org.cyclonedx.bom.gradle.plugin
    alias(libs.plugins.cyclonedx) // id("org.cyclonedx.bom") version "2.2.0"

    // 오픈 소스 라이선스 리포트 만들기
    alias(libs.plugins.license.report) // id("com.github.jk1.dependency-license-report") version "2.5"
}

group = "com.snc.zero"
version = "0.1-beta"

buildscript {
    extra.apply {
        // Gradle JVM : File > Settings > Build, Execution, Deployment > Build Tools > Gradle > "Gradle JVM"
        set("javaVersion", JavaVersion.VERSION_17)
        set("kotlinVersion", "2.0") // org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
    }
}

repositories {
    mavenCentral()
    //maven("https://oss.sonatype.org/content/repositories/snapshots")
    //maven {
    //    url = uri("https://plugins.gradle.org/m2/")
    //}
}

dependencies {
    implementation(platform(rootProject.libs.kotlin.bom))
    implementation(rootProject.libs.kotlin.stdlib)
    implementation(rootProject.libs.kotlin.build.tools.impl)
    //implementation("org.owasp:dependency-check-gradle:5.3.0")

    //testImplementation(kotlin("test"))
    //testImplementation(rootProject.libs.junit.jupiter.api)
    //testRuntimeOnly(rootProject.libs.junit.platform.launcher)
    //testRuntimeOnly(rootProject.libs.junit.jupiter.engine)
}

// 버전 강제화 (force):
configurations.all {
    resolutionStrategy {
        // logback 버전 강제화
        force("ch.qos.logback:logback-core:1.5.13")
        force("ch.qos.logback:logback-classic:1.5.13")
    }
}

// Gradle을 사용할 때, dependency verification 의존성 검증 기능을 비활성화 조치.
//configurations.all {
//    resolutionStrategy {
//        // Sensitive: dependency verification is disabled
//        disableDependencyVerification()
//    }
//}

//dependencyLocking {
//    lockAllConfigurations()
//}
//
//dependencyVerification {
//    configuration {
//        // 의존성 검증 비활성화
//        verificationMode.set(VerificationMode.DISABLED)
//    }
//}

//dependencyCheck {
//    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
//}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = rootProject.extra["javaVersion"] as JavaVersion
    targetCompatibility = rootProject.extra["javaVersion"] as JavaVersion
}

kotlin {
    sourceSets.all {
        println("Source set: $name")
        println("Depends on: ${dependsOn.joinToString(", ") { it.name }}")
        println("---")
    }

    compilerOptions {
        languageVersion.set(KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
        apiVersion.set(KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
    }

    println("[kotlin-compilerOptions] KotlinVersion = " + rootProject.extra["kotlinVersion"] as String + " --> " + KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String) + " --- " + KotlinVersion.KOTLIN_2_1 + " : " + KotlinVersion.fromVersion("2.1"))
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-opt-in=kotlin.ExperimentalStdlibApi")
    }
}

//val compileTestKotlin: KotlinCompile by tasks
//compileTestKotlin.compilerOptions {
//    languageVersion.set(KotlinVersion.KOTLIN_2_1)
//}

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

/*
sourceSets {
    val main by getting {
        java {
            setSrcDirs(listOf("src/main/java", "src/main/kotlin"))
        }
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
    val test by getting {
        java {
            setSrcDirs(listOf("src/test/java", "src/test/kotlin"))
        }
        resources {
            setSrcDirs(listOf("src/test/resources"))
        }
    }
}
 */

///////////////////////////////////////////////////////////
// KtLint
// -GitHub: https://github.com/JLLeitschuh/ktlint-gradle
//          https://github.com/pinterest/ktlint
// -Rule: https://pinterest.github.io/ktlint/latest/rules/standard/
//
//configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
ktlint {
    version.set("1.5.0") // CLI 최신버전 확인: https://pinterest.github.io/ktlint/latest/
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true) // 콘솔 출력 활성화
    //ignoreFailures.set(true)  // true: 오류 무시하고 계속 진행
    //enableExperimentalRules.set(true) // 실험적 규칙 활성화

    filter {
        //exclude("**/generated/**") // 생성된 코드 제외
        //exclude("**/build/**") // 생성된 코드 제외
        //exclude("**/*Test.kt") // 특정 파일 제외
        //include("**/kotlin/**") // kotlin 소스만 포함
    }

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
}

// 특정 task에서만 오류 무시:
//tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask> {
//    ignoreFailures = true
//}

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
// clean Task 에 커스텀 Task 추가
// Cannot add task 'clean' as a task with that name already exists.
//tasks.register("clean") { }
tasks.named("clean") {
    doFirst {
        delete("$projectDir/out")
        delete("$projectDir/gradle/verification-metadata.xml")
    }
    doLast {
        val buildDir = project.layout.buildDirectory.asFile.get()
        println("##############################")
        println("Deleting build directory: $buildDir")
        println("##############################")

        // Recursively delete the build folder
        buildDir.deleteRecursively()
    }
}

///////////////////////////////////////////////////////////
// compileJava 사전 Task
tasks.register<Exec>("deleteDSStoreShellScript") {
    description = "This is a shell task that deletes the '.DS_Store' file when building a project."
    group = JavaBasePlugin.BUILD_TASK_NAME
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "del_ds_store.bat")
    } else {
        commandLine("sh", "./del_ds_store.sh")
    }

    //delete file('src/main/generated') // 인텔리제이 Annotation processor 생성물 생성 위치
}

tasks.named("compileJava") {
    dependsOn("deleteDSStoreShellScript")
}

///////////////////////////////////////////////////////////
// 빌드 이후 후속 Task
// 의존성 검증 메타데이터 생성 task 정의
tasks.register<Exec>("generateVerificationMetadata") {
    group = "verification"
    description = "모든 하위 프로젝트 빌드 완료 후 의존성 검증 메타데이터 파일 생성"

    // 프로젝트 루트 디렉토리에서 실행
    workingDir = projectDir

    // OS에 따라 적절한 명령어 설정
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "gradlew", "--write-verification-metadata", "sha256")
    } else {
        commandLine("./gradlew", "--write-verification-metadata", "sha256")
    }

    doLast {
        println("의존성 검증 메타데이터가 생성되었습니다.")
    }
}

// root build task가 완료된 후 generateVerificationMetadata task 실행
tasks.named("build") {
    finalizedBy("generateVerificationMetadata")
}

///////////////////////////////////////////////////////////
// Sub-Projects Settings
subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    //apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    repositories {
        mavenCentral()
    }

    ktlint {
        version.set("1.5.0")
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)

        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        }
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
        useJUnitPlatform()

        // 실행순서 : test -> jacocoTestReport -> jacocoTestCoverageVerification
        finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
    }

    //// library-zero-file-manager-kotlin 모듈의 test task 비활성화
    //if (project.name == "library-zero-file-manager-kotlin") {
    //    tasks.named<Test>("test") {
    //        enabled = false
    //    }
    //}

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

    //++
    /*
    detekt {
        //buildUponDefaultConfig = true // preconfigure defaults
        //allRules = false // activate all available (even unstable) rules.
        // https://detekt.dev/docs/introduction/configurations/
        //config.setFrom("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
        //baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
    }

    tasks.withType<Detekt>().configureEach {
        reports {
            html.required.set(true) // observe findings in your browser with structure and code snippets
            xml.required.set(false) // checkstyle like format mainly for integrations like Jenkins
            sarif.required.set(false) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
            md.required.set(true) // simple Markdown format
        }
    }

    // Kotlin DSL
    tasks.withType<Detekt>().configureEach {
        jvmTarget = "1.8"
    }
    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = "1.8"
    }
     */
    //--
}
