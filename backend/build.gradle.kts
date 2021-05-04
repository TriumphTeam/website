import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-RC"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

group = "dev.triumphteam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "1.5.4"
val logbackVersion = "1.2.1"
val junitVersion = "5.6.0"
val assertjVersion = "3.19.0"
val exposedVersion = "0.29.1"

dependencies {
    implementation(kotlin("stdlib"))

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    implementation("com.ryanharter.ktor:ktor-moshi:1.0.1")
    implementation("io.ktor:ktor-gson:$ktorVersion")

    /*implementation("com.zaxxer:HikariCP:4.0.1")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.2")*/

    // Testing
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
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
            useIR = true
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}