plugins {
    kotlin("jvm")
    id("jacoco")
}

group = "com.snc.zero"
version = "0.1-beta"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library-zero-logger-kotlin"))
    implementation(project(":library-zero-extension-kotlin"))
    implementation(libs.bouncycastle.bcprov)
    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.core)
    implementation(libs.jackson.datatype.jsr310)

    testImplementation(project(":library-zero-test-kotlin"))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.platform.launcher)
    //testRuntimeOnly(libs.junit.jupiter.engine)
}

kotlin {
    // 상황에 따라 모듈별 JVM 버전을 지정합니다
    //jvmToolchain(rootProject.extra["jvmToolchain"] as Int)

    compilerOptions {
        // Kotlin 언어 버전은 대부분 명시 불필요, 점진적 마이그레이션이 필요한 경우만, "엄격한 버전 제어" 설정 권장:
        //languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
        //apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion(rootProject.extra["kotlinVersion"] as String))
    }
}

tasks.named("clean") {
    doFirst {
        delete("$projectDir/out")
    }
}

//tasks.named<Test>("test") {
//    enabled = false
//}

//
// CLI 테스트:
// $ ./gradlew :library-zero-crypto-kotlin:test --no-daemon --rerun-tasks -PHMAC_SECRET_KEY=c36a1aaf4e554d1b94449437d22d8a53360e819f731c7acda15b4ed63665f0de
// $ ./gradlew :library-zero-crypto-kotlin:test --tests "*.Ed25519SignerTest" --no-daemon --rerun-tasks

// IntelliJ에서 테스트 실행 시 값을 못 읽을 경우
// - Settings > Build, Execution, Deployment > Build Tools > Gradle → "Run tests using" 을 Gradle로 변경

// hmac key 살장
val hmacKey = project.findProperty("HMAC_SECRET_KEY")?.toString()
    ?: error("HMAC_SECRET_KEY 프로퍼티가 설정되지 않았습니다. gradle.properties 또는 -PHMAC_SECRET_KEY=값 으로 전달해주세요.")

require(hmacKey.isNotBlank()) { "HMAC_SECRET_KEY 값이 비어있습니다." }

tasks.withType<JavaExec> {
    systemProperty("HMAC_SECRET_KEY", hmacKey)
}

tasks.withType<Test> {
    //println(">>> HMAC_SECRET_KEY from gradle: $hmacKey") // 그래들이 읽는지 확인
    //println(">>> env: $environment") // 환경변수 전달 확인
    environment("HMAC_SECRET_KEY", hmacKey)
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = false // println 출력 보이게
    }
}
