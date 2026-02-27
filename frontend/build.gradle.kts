plugins {
    id("website.vite")
}

root {
    configureMultiplatform {
        js {
            target {
                browser()
                binaries.executable()
                useEsModules()
            }

            dependencies {
                implementation("dev.triumphteam:horizon-core:1.0.0-SNAPSHOT")
                implementation(npm("@tailwindcss/vite", "4.1.18"))
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.json)
                implementation(libs.ktor.client.negociation)
            }
        }
    }
}
