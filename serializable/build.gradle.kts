plugins {
    id("website.multiplatform")
}

root {
    configureMultiplatform {
        jvm()

        js {
            target {
                browser()
                useEsModules()
                binaries.library()
            }
        }

        common {
            dependencies {
                api(libs.serialization.core)
                api(libs.ktor.resources)
            }
        }
    }
}
