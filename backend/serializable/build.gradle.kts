import java.util.UUID

plugins {
    id("backend.multiplatform")
    id("org.danilopianini.npm.publish") version "4.0.7"
}

kotlin {
    jvm()
    js(IR) {
        nodejs()
        useEsModules()
        binaries.library()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.serialization.core)
            }
        }
    }
}

npmPublish {
    registries {
        // For registries expecting an authentiation token, use authToken
        register("npmjs") {
            uri.set("https://registry.npmjs.org")
        }

        packages {
            named("js") {
                packageName.set("triumph-docs-serializable")
                scope.set("lichthund")
                version.set(project.version.toString() + UUID.randomUUID().toString().substring(0, 5))
            }
        }
    }
}
