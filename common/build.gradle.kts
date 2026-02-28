plugins {
    id("website.jvm")
}

dependencies {
    api(libs.zip)

    api(kotlin("stdlib"))
    api(libs.serialization.json)
    api(libs.serialization.hocon)
    api(libs.coroutines)

    api(projects.websiteSerializable)
}
