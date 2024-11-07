plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "multi-module-project"

include("library-zero-core-kotlin")
include("library-zero-crypto-kotlin")
include("library-zero-file-manager-kotlin")
include("library-zero-identification-kotlin")
include("library-zero-logger-kotlin")
include("library-zero-test-kotlin")
include("library-zero-validation-kotlin")
include("library-zero-xss-kotlin")
include("library-zero-pdfbox-kotlin")
include("library-zero-markdown-kotlin")
include("library-zero-personal-info-kotlin")
include("library-zero-cache-kotlin")
include("library-zero-jackson-kotlin")
include("library-zero-age-kotlin")
include("library-zero-hangul-kotlin")
include("library-zero-prizedraw-kotlin")
