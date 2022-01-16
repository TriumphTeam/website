@file:OptIn(ExperimentalPathApi::class)

package dev.triumphteam.backend.feature

import dev.triumphteam.backend.database.Projects
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.ExperimentalPathApi

class Placeholders(application: Application) {

    private val placeholders = mutableListOf<Placeholder>(VersionPlaceholder(application))

    fun replace(project: String, original: String): String {
        var replace = original

        for (placeholder in placeholders) {
            replace = placeholder.replace(project, replace)
        }

        return replace
    }

    companion object Feature : ApplicationFeature<Application, Placeholders, Placeholders> {
        override val key = AttributeKey<Placeholders>("Placeholder")

        override fun install(pipeline: Application, configure: Placeholders.() -> Unit): Placeholders {
            return Placeholders(pipeline).apply(configure)
        }
    }
}

interface Placeholder {
    fun replace(project: String, original: String): String
}

class VersionPlaceholder(application: Application) : Placeholder {

    private val placeholder = "%version%"
    private var versions = mutableMapOf<String, String>()

    init {
        retrieveVersion()
    }

    override fun replace(project: String, original: String): String {
        val version = versions[project] ?: return original
        return original.replace(placeholder, version)
    }

    private fun retrieveVersion() {
        transaction {
            Projects.selectAll().forEach {
                updateVersion(it[Projects.id], it[Projects.version])
            }
        }
    }

    private fun updateVersion(project: String, version: String) {
        versions[project] = version
    }

}
