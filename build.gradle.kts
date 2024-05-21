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

// Gradle의 Kotlin DSL을 사용하여 shell 스크립트를 실행하는 방법을 설명드리겠습니다.
// Kotlin DSL을 사용하는 경우 build.gradle.kts 파일에 해당 내용을 추가합니다.
//
// 1. 실행 함수 등록
tasks.register<Exec>("runShellScript") {
    println(">>>>> task runShellScript")

    // shell 스크립트 실행 시 환경 변수를 설정하려면 environment 메소드를 사용할 수 있습니다.
    //environment("MY_VAR", "myValue")

    // 윈도우와 유닉스 시스템에서 모두 동작하도록 하기 위해 OS를 체크하고 적절한 명령어를 사용하는 방법입니다.
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "del_ds_store.bat")
    } else {
        commandLine("sh", "./del_ds_store.sh")
    }
}
// 2. 빌드 과정 중 특정 단계에서 실행
//tasks.named("compileJava") {
//    println(">>>>> task compileJava")
//    finalizedBy("runShellScript")
//}
//또는 특정 단계에 종속된 작업을 직접 정의할 수도 있습니다.
tasks.named("compileJava") {
    println(">>>>> task dependsOn(compileJava)")
    dependsOn("runShellScript")
}