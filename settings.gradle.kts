plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "multi-module-project"
include("library-zero-kotlin")
include("library-zero-identification-kotlin")
