package dev.triumphteam.backend.feature

import dev.triumphteam.backend.database.Entries
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.func.SUMMARY_FILE_NAME
import dev.triumphteam.backend.func.titleCase
import dev.triumphteam.backend.func.warn
import dev.triumphteam.markdown.summary.Entry
import dev.triumphteam.markdown.summary.Header
import dev.triumphteam.markdown.summary.Link
import dev.triumphteam.markdown.summary.SummaryParser
import dev.triumphteam.markdown.summary.type
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.feature
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@OptIn(ExperimentalUnsignedTypes::class)
class Project(private val parser: SummaryParser) {

    fun loadAll(repoFolder: File) {
        CoroutineScope(IO).launch {
            val projects = repoFolder.listFiles()?.filter { it.isDirectory } ?: return@launch
            projects.forEach { projectFile ->
                val summaryMd = projectFile.listFiles()
                    ?.filter { it.extension == "md" }
                    ?.find { it.name == SUMMARY_FILE_NAME }
                    ?: run {

                        // Removes the project as it's unavailable
                        transaction {
                            Projects.deleteWhere { Projects.name eq projectFile.name }
                        }

                        warn {
                            """
                                Could not find summary for project "${projectFile.name}", ignoring.
                            """.trimIndent()
                        }

                        return@forEach
                    }

                val projectName = projectFile.nameWithoutExtension
                val projectId = transaction {
                    Projects.insertIgnoreAndGetId {
                        it[name] = projectName
                    } ?: Projects.select {
                        Projects.name eq projectName
                    }.firstOrNull()?.get(Projects.id)
                } ?: return@forEach

                transaction {
                    Entries.deleteWhere { Entries.project eq projectId }

                    val summary = parser.parse(summaryMd.readText())
                    summary.insertEntries(projectId)
                }

            }
        }
    }

    private fun List<Entry>.insertEntries(projectId: EntityID<Int>) {
        forEachIndexed { pos, entry ->
            transaction {
                insertEntry(entry, projectId, pos.toUInt())
            }
        }
    }

    private fun insertEntry(
        entry: Entry,
        project: EntityID<Int>,
        position: UInt,
        type: UByte? = null,
        parent: EntityID<Int>? = null
    ): EntityID<Int> {
        return Entries.insertAndGetId {
            it[this.project] = project

            if (entry is Header) {
                it[literal] = entry.literal.titleCase()
            } else if (entry is Link) {
                it[literal] = entry.literal.titleCase()
                it[destination] = entry.destination
                it[indent] = entry.indent
            }

            it[this.type] = type ?: entry.type
            it[this.parent] = parent
            it[this.position] = position
        }
    }

    /**
     * Empty config, not much needed tbh
     */
    class Configuration

    /**
     * Feature companion
     */
    companion object Feature : ApplicationFeature<Application, Project, Project> {
        override val key = AttributeKey<Project>("Project")

        override fun install(pipeline: Application, configure: Project.() -> Unit): Project {
            val parser = pipeline.feature(SummaryParser)
            return Project(parser).apply(configure)
        }
    }

}