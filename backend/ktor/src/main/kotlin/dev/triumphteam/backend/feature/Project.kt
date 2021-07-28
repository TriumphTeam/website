package dev.triumphteam.backend.feature

import dev.triumphteam.backend.database.Contents
import dev.triumphteam.backend.database.Entries
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.func.MARKDOWN_FILE_EXTENSION
import dev.triumphteam.backend.func.SUMMARY_FILE_NAME
import dev.triumphteam.backend.func.checksum
import dev.triumphteam.backend.func.titleCase
import dev.triumphteam.backend.func.warn
import dev.triumphteam.markdown.summary.Entry
import dev.triumphteam.markdown.summary.Header
import dev.triumphteam.markdown.summary.Link
import dev.triumphteam.markdown.summary.SummaryParser
import dev.triumphteam.markdown.summary.type
import dev.triumphteam.markdown.summary.writer.InvalidSummaryException
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.feature
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.andWhere
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

    fun loadAll(repoFolder: File) = CoroutineScope(IO).launch {
        val projects = repoFolder.listFiles()?.filter { it.isDirectory } ?: return@launch
        projects.forEach { projectFile ->
            val files = projectFile.listFiles()
                ?.filter { it.extension == MARKDOWN_FILE_EXTENSION }
                ?: return@forEach

            val summaryMd = files.find { it.name == SUMMARY_FILE_NAME } ?: run {
                // Removes the project as it's invalid or unavailable
                transaction {
                    Projects.deleteWhere { Projects.name eq projectFile.name }
                }

                warn { "Could not find summary for project ${projectFile.name}, ignoring!" }
                return@forEach
            }

            // Gets the current project name
            val projectName = projectFile.nameWithoutExtension

            transaction {

                val projectId = Projects.select {
                    Projects.name eq projectName
                }.firstOrNull()?.get(Projects.id)
                    ?: Projects.insertAndGetId {
                        it[name] = projectName
                    }

                if (!insertSummary(projectId, summaryMd.readText())) {
                    warn { "Could not parse project $projectName, project will be ignored!" }
                    rollback()
                    return@transaction
                }

                files.filter { it.name != SUMMARY_FILE_NAME }.forEach { pageFile ->
                    val content = Contents
                        .select { Contents.project eq projectId }
                        .andWhere { Contents.url eq pageFile.nameWithoutExtension }
                        .firstOrNull()

                    // Doesn't exist, insert
                    if (content == null) {
                        if (!insertPage(projectId, pageFile)) rollback()
                        return@transaction
                    }

                    // No change, ignore
                    if (content[Contents.checksum] == pageFile.checksum()) return@transaction

                    // Delete existing one
                    Contents.deleteWhere { Contents.id eq content[Contents.id] }
                    if (!insertPage(projectId, pageFile)) rollback()
                }
            }
        }
    }

    private fun insertSummary(projectId: EntityID<Int>, summaryText: String): Boolean {
        Entries.deleteWhere { Entries.project eq projectId }

        val summary = try {
            parser.parse(summaryText)
        } catch (exception: InvalidSummaryException) {
            return false
        }

        summary.insertEntries(projectId)
        return true
    }

    private fun insertPage(projectId: EntityID<Int>, pageFile: File): Boolean {
        // TODO move parser out
        val parser: Parser = Parser.builder().build()
        val document: Node = parser.parse(pageFile.readText())
        val renderer = HtmlRenderer.builder().build()

        // TODO the id will be used later I think
        Contents.insertIgnoreAndGetId {
            it[project] = projectId
            // TODO make sure name doesn't have spaces
            it[url] = pageFile.nameWithoutExtension
            it[content] = renderer.render(document)
            it[checksum] = pageFile.checksum()
        }

        return true
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