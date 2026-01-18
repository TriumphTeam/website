import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("backend.jvm")
    id("io.ktor.plugin") version "3.3.3"
}

application {
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
    mainClass.set("dev.triumphteam.backend.ApplicationKt")
}

dependencies {
    // implementation(projects.websiteCommon)

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
