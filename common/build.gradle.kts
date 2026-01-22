plugins {
    id("website.jvm")
}

dependencies {
    api(libs.zip)
    api(libs.ktor.resources)

    api(kotlin("stdlib"))
    api(libs.serialization.json)
    api(libs.serialization.hocon)
    api(libs.coroutines)

    // api(projects.websiteSerializable)
}
