package dev.triumphteam.backend.feature

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
class Project {

    fun loadAll(repoFolder: File) {
        GlobalScope.launch(Dispatchers.IO) {
            val projects = repoFolder.listFiles()?.filter { it.isDirectory } ?: return@launch
            projects.forEach { project ->
                val index = project.listFiles()?.find { it.name == "SUMMARY.md" } ?: return@forEach
                println(index)
            }
        }
    }

    /**
     * Empty config, not much needed tbh
     */
    class Configuration

    /**
     * Feature companion
     */
    companion object Feature : ApplicationFeature<Application, Configuration, Project> {
        override val key = AttributeKey<Project>("Project")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Project {
            return Project()
        }
    }

}