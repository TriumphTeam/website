import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "dev.triumphteam"
version = "1.0-SNAPSHOT"

subprojects {

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("kotlinx-serialization")
    }

    repositories {
        mavenCentral()
        maven("https://repo.mattstudios.me/artifactory/public/")
    }

    // TODO organize versions mess
    val ktorVersion = "1.5.4"
    val logbackVersion = "1.2.1"
    val junitVersion = "5.6.0"
    val assertjVersion = "3.19.0"

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("io.ktor:ktor-serialization:$ktorVersion")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation("org.assertj:assertj-core:${assertjVersion}")
        testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "15"
                freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }

    }

}