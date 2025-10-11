plugins {
    id("backend.multiplatform")
    id("org.danilopianini.npm.publish") version "4.1.3"
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

        /*nodeHome = project.objects.directoryProperty().fileValue(File("C:\\Users\\xpsyk\\.gradle\\nodejs\\node-v22.0.0-win-x64"))
        nodeBin = nodeHome.file("node.exe")
        npmBin = nodeHome.file("node_modules/npm/bin/npm-cli.js")*/

        packages {
            named("js") {
                packageName.set("triumph-docs-serializable")
                scope.set("lichthund")
                version.set(project.version.toString())
            }
        }
    }
}
