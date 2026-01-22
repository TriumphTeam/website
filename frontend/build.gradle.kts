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
            }
        }
    }
}
