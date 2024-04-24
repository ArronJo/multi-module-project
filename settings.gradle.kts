plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "multi-module-project"

include("library-zero-core-kotlin")
include("library-zero-test-kotlin")
include("library-zero-logger-kotlin")
