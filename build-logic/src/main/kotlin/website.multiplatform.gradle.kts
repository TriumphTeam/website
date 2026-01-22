import dev.triumphteam.root.KotlinOpt
import dev.triumphteam.root.repository.Repository
import dev.triumphteam.root.repository.applyRepo
import gradle.kotlin.dsl.accessors._a281364f58800ed1dae7011dddd08807.root
import org.gradle.accessors.dm.LibrariesForLibs


val libs = the<LibrariesForLibs>()

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("dev.triumphteam.root")
}

repositories {
    mavenCentral()
    applyRepo(Repository.TRIUMPH_SNAPSHOTS)
}

/*dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.serialization.json)
    implementation(libs.serialization.hocon)
    implementation(libs.coroutines)
}*/

root {
    configureMultiplatform {
        explicitApi()
        optIn(KotlinOpt.STD)
        previewAll()
    }
}

