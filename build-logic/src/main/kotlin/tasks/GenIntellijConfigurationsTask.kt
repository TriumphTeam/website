package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

/** Simple task to generate IntelliJ run configurations for the project. */
abstract class GenIntellijConfigurationsTask : DefaultTask() {

    private companion object {
        private const val RUN_EXTENSION = "run.xml"
    }

    @TaskAction
    open fun execute() {
        // Grab the project directory and resolve the idea configurations path.
        val ijRunDirectory = project.rootDir.toPath().resolve(".idea").resolve("runConfigurations")
            .also { it.createDirectories() }

        // Then for each configuration paste the template in.
        Configurations.entries.forEach { configuration ->
            ijRunDirectory.resolve("${configuration.fileName}.$RUN_EXTENSION").writeText(configuration.template)
        }
    }

    private enum class Configurations(val fileName: String, val template: String) {
        POSTGRES(fileName = "RunPostgress", template = RUN_POSTGRES),
        BACKEND(fileName = "RunBackend", template = RUN_BACKEND),
        DOCS(fileName = "RunDocsUpload", template = RUN_DOCS_UPLOAD),
        FRONTEND(fileName = "RunFrontend", template = RUN_FRONTEND),
    }
}
