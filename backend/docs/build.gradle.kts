import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("backend.base")
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenCentral()
    maven("https://repo.triumphteam.dev/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(projects.common)

    implementation(libs.bundles.ktor.client)

    implementation(libs.bundles.logger)
    implementation(libs.bundles.commonmark)
    implementation(libs.commons.cli)

    implementation(projects.scripting)

    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlin:kotlin-main-kts")
    implementation("dev.triumphteam:triumph-gui-paper-kotlin:4.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.16.0")
    implementation("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
}

application {
    mainClass.set("dev.triumphteam.website.docs.ApplicationKt")
}

tasks {

    withType<ShadowJar> {
        archiveFileName.set("docs.jar")
    }
}
