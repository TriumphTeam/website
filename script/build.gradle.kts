import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

plugins {
    kotlin("multiplatform")
}

kotlin {

    explicitApi()

    js(IR) {
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                // Bit hacky but we do what we gotta do with what we are given :'))
                val resourcesDir = project(":backend").projectDir.resolve("src/main/resources/static/scripts")
                outputDirectory.set(resourcesDir)
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:1.0.0-pre.760")
            }
        }
    }
}
