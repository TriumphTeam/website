package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class PrepareJs : DefaultTask() {

    @get:InputDirectory
    abstract val jsDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun prepare() {
        val jsFile = jsDir.get().asFile.listFiles()?.find { it.name.endsWith(".js") }
        requireNotNull(jsFile) {
            "Could not find JS file!!"
        }

        val scriptFile = outputDir.get().asFile.resolve("static/scripts/script.js")
        jsFile.copyTo(scriptFile, overwrite = true)
    }
}
