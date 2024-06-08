import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("backend.base")
    id("io.ktor.plugin") version "2.3.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val ktorVersion = "1.6.3"
val exposedVersion = "0.37.2"

dependencies {
    implementation(projects.markdown)
    implementation(libs.bundles.logger)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)

    implementation("com.google.guava:guava:30.1.1-jre")
}

val main = "dev.triumphteam.backend.ApplicationKt"

application {
    mainClass.set(main)
}

tasks {

    withType<ShadowJar> {
        manifest {
            attributes["Main-Class"] = main
        }
        archiveBaseName.set("website.jar")
        mergeServiceFiles()
    }
}

kotlin {
    explicitApi()
}
