plugins {
    id("backend.configure")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    maven("https://repo.triumphteam.dev/snapshots/")
}

kotlin {
    explicitApi()
}
