plugins {
    id("backend.base")
}

dependencies {
    implementation(projects.common)

    implementation(libs.bundles.ktor.client)

    implementation(libs.bundles.logger)
    implementation(libs.bundles.commonmark)
    implementation(libs.commons.cli)

    implementation("net.lingala.zip4j:zip4j:2.11.5")
}
