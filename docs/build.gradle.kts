plugins {
    id("backend.base")
}

dependencies {
    implementation(projects.common)

    implementation(libs.bundles.ktor.client)

    implementation(libs.bundles.logger)
    implementation(libs.bundles.commonmark)
    implementation(libs.commons.cli)
}
