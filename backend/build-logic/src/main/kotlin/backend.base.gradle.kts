import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.serialization.json)
    implementation(libs.serialization.hocon)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
