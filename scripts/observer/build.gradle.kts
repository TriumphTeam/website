plugins {
    id("backend.script")
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation(libs.wrapper.browser)
            }
        }
    }
}
