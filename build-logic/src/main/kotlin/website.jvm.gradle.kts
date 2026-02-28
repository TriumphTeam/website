import dev.triumphteam.root.KotlinOpt
import dev.triumphteam.root.repository.Repository
import dev.triumphteam.root.repository.applyRepo
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("dev.triumphteam.root")
}

repositories {
    mavenCentral()
    applyRepo(Repository.TRIUMPH_SNAPSHOTS)
}

root {
    configureKotlin {
        jvmVersion(25)
        explicitApi()
        optIn(KotlinOpt.STD)
        previewAll()
    }
}

