package dev.triumphteam.backend.feature

import dev.triumphteam.backend.database.Projects
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class Placeholders {

    private val placeholders = mutableListOf<Placeholder>(VersionPlaceholder())

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
            return Placeholders().apply(configure)
        }
    }
}

interface Placeholder {
    fun replace(project: String, original: String): String
}

class VersionPlaceholder : Placeholder {

    private val placeholder = "%version%"
    private var versions = mutableMapOf<String, String>()

    init {
        retrieveVersion()
    }

    override fun replace(project: String, original: String): String {
        val version = versions[project] ?: original
        return original.replace(placeholder, version)
    }

    private fun retrieveVersion() {
        transaction {
            Projects.selectAll().forEach {
                versions[it[Projects.name]] = it[Projects.version]
            }
        }
    }

}
