import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "dev.triumphteam"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.mattstudios.me/artifactory/public/")
    }
}

subprojects {

    apply {
        plugin("kotlin")
        plugin("kotlinx-serialization")
    }

    // TODO organize versions mess
    val ktorVersion = "1.6.3"
    val logbackVersion = "1.2.1"
    val junitVersion = "5.6.0"
    val assertjVersion = "3.19.0"

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
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
                jvmTarget = "17"
                freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }

    }

}