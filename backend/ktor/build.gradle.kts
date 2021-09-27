plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

val ktorVersion = "1.6.3"
val exposedVersion = "0.30.1"

dependencies {
    implementation(project(":markdown"))
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

    implementation("me.mattstudios:triumph-config:1.0.5-SNAPSHOT")

    implementation("net.lingala.zip4j:zip4j:1.3.2")

    val commonmark = "0.18.0"

    // MD things
    implementation("org.commonmark:commonmark:$commonmark")
    implementation("org.commonmark:commonmark-ext-autolink:$commonmark")
    implementation("org.commonmark:commonmark-ext-gfm-strikethrough:$commonmark")
    implementation("org.commonmark:commonmark-ext-gfm-tables:$commonmark")
    implementation("org.commonmark:commonmark-ext-task-list-items:$commonmark")

    implementation("com.google.guava:guava:30.1.1-jre")

    implementation("com.zaxxer:HikariCP:4.0.1")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.2")

    // Testing
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}