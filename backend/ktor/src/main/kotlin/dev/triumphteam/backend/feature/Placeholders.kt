@file:OptIn(ExperimentalPathApi::class)

package dev.triumphteam.backend.feature

import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.events.GithubPush
import dev.triumphteam.backend.func.SCOPE
import dev.triumphteam.backend.func.log
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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

        application.listening {
            on<GithubPush> {
                SCOPE.launch {
                    val release = github.getRelease(project) ?: return@launch
                    transaction {
                        Projects.update({ Projects.github eq project }) {
                            it[version] = release.version
                        }
                        updateVersion(project, release.version)
                    }
                }
            }
        }
    }

    override fun replace(project: String, original: String): String {
        val version = versions[project] ?: return original
        return original.replace(placeholder, version)
    }

    private fun retrieveVersion() {
        SCOPE.launch {
            delay(1500)
            log { "Retrieving versions" }
            transaction {
                Projects.selectAll().forEach {
                    updateVersion(it[Projects.name], it[Projects.version])
                }
            }
        }
    }

    private fun updateVersion(project: String, version: String) {
        versions[project] = version
    }

}
