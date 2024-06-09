import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.backend.common.serialization.KotlinIrLinker
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink
import tasks.PrepareJs

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
        browser()
        binaries.executable()
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

tasks {

    val prepareJs = register<PrepareJs>("prepareJs") {

        val jsTask = named<Copy>("jsBrowserDistribution").get()
        val resourcesTask = named<Copy>("processResources").get()

        dependsOn(jsTask)
        dependsOn(resourcesTask)

        jsDir.set(jsTask.destinationDir)
        outputDir.set(resourcesTask.destinationDir)
    }

    named("jvmJar") {
        dependsOn(prepareJs)
    }

    withType<ShadowJar> {
        dependsOn(prepareJs)
    }

    jar {
        dependsOn(prepareJs)
    }
}
