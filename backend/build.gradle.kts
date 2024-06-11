plugins {
    id("backend.base")
    id("io.ktor.plugin") version "2.3.10"
}
val main = "dev.triumphteam.backend.ApplicationKt"

application {
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
    mainClass.set(main)
}

dependencies {
    implementation(projects.common)

    implementation(libs.bundles.logger)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)

    implementation("com.google.guava:guava:30.1.1-jre")
}

tasks {

    /*val prepareJs = register<PrepareJs>("prepareJs") {

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
    }*/
}
