import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("backend.base")
    id("io.github.goooler.shadow") version "8.1.7"
}

dependencies {
    implementation(projects.common)

    implementation(libs.bundles.ktor.client)

    implementation(libs.bundles.logger)
    implementation(libs.bundles.commonmark)
    implementation(libs.commons.cli)
}

application {
    mainClass.set("dev.triumphteam.website.docs.ApplicationKt")
}

tasks {

    withType<ShadowJar> {
        archiveFileName.set("docs.jar")
    }
}
