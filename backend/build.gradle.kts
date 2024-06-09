plugins {
    id("backend.multiplatform")
    id("io.ktor.plugin") version "2.3.10"
}
val main = "dev.triumphteam.backend.ApplicationKt"

application {
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
    mainClass.set(main)
}

kotlin {
    explicitApi()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    jvm {
        withJava()
    }

    js(IR) {

        browser {
            binaries.executable()
        }
    }

    sourceSets {

        jvmMain {
            dependencies {
                implementation(libs.bundles.logger)
                implementation(libs.bundles.ktor.client)
                implementation(libs.bundles.ktor.server)

                implementation("com.google.guava:guava:30.1.1-jre")
            }
        }
    }
}
