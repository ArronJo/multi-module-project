plugins {
    // Gradle에서 Java 런타임(JDK)을 자동으로 찾고 관리해주는 플러그인
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "multi-module-project"

include("library-zero-age-kotlin")
include("library-zero-cache-kotlin")
include("library-zero-core-kotlin")
include("library-zero-crypto-kotlin")
include("library-zero-file-manager-kotlin")
include("library-zero-hangul-kotlin")
include("library-zero-identification-kotlin")
include("library-zero-jackson-kotlin")
include("library-zero-logger-kotlin")
include("library-zero-markdown-kotlin")
include("library-zero-pdfbox-kotlin")
include("library-zero-pdfbox3-kotlin")
include("library-zero-personal-info-kotlin")
include("library-zero-prizedraw-kotlin")
include("library-zero-test-kotlin")
include("library-zero-validation-kotlin")
include("library-zero-xss-kotlin")
include("library-zero-e2e-kotlin")
include("library-zero-password-generator")
include("library-zero-extension-kotlin")
include("library-zero-similarity-kotlin")
include("library-zero-etc-kotlin")
include("library-zero-color-kotlin")
include("library-zero-keymanager-kotlin")
include("library-zero-prompt-security-kotlin")
