import tasks.GenIntellijConfigurationsTask

plugins {
    kotlin("jvm")
}

/** Used by CI to get the latest [project#version]. */
tasks.register("ciVersion") {
    println(project.version)
}

/** Main task for generating the intellij run configurations. */
tasks.register<GenIntellijConfigurationsTask>("genIntellijRuns") {
    group = "triumph"
    description = "Generates the run configurations needed for this project."
}
