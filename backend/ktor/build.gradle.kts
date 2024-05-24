plugins {
    id("backend.base")
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val ktorVersion = "1.6.3"
val exposedVersion = "0.37.2"

dependencies {
    implementation(projects.markdown)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)

    implementation("net.lingala.zip4j:zip4j:1.3.2")
    implementation("com.google.guava:guava:30.1.1-jre")
}

tasks {

    shadowJar {
        archiveFileName.set("backend.jar")
    }

    application {
        mainClass.set("dev.triumphteam.backend.ApplicationKt")
    }
}
