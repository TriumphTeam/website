import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("backend.base")
    id("io.ktor.plugin") version "2.3.10"
}

application {
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
    mainClass.set("dev.triumphteam.backend.ApplicationKt")
}

dependencies {
    implementation(projects.common)

    implementation(libs.bundles.logger)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.database)

    implementation(libs.caffeine)
}

tasks {
    withType<ShadowJar> {
        archiveFileName.set("backend.jar")
    }
}
