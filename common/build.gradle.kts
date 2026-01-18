plugins {
    id("backend.jvm")
}

dependencies {
    api(libs.zip)
    api(libs.ktor.resources)
    // api(projects.websiteSerializable)
}
