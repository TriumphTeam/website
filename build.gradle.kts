plugins {
    id("backend.parent")
}

/** Used by CI to get the latest [project#version]. */
tasks.register("ciVersion") {
    println(project.version)
}
