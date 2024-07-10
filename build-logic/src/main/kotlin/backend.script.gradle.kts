import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

plugins {
    id("backend.multiplatform")
}

kotlin {
    js(IR) {
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                // Bit hacky, but we do what we gotta do with what we are given :'))
                val resourcesDir = project(":backend").projectDir.resolve("src/main/resources/static/scripts")
                outputDirectory.set(resourcesDir)
            }

            webpackTask {
                mainOutputFileName.set("${project.name.split("-").last()}.js")
            }
        }

        binaries.executable()
    }
}
