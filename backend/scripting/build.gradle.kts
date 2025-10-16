plugins {
    id("backend.base")
}

repositories {
    maven("https://repo.triumphteam.dev/snapshots/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    implementation("dev.triumphteam:triumph-gui-kotlin:4.0.0-SNAPSHOT")
}
