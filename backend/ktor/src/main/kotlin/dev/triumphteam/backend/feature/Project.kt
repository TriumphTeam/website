package dev.triumphteam.backend.feature

import dev.triumphteam.backend.CONFIG
import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.database.Contents
import dev.triumphteam.backend.database.Contents.page
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.database.Summaries
import dev.triumphteam.backend.func.JSON
import dev.triumphteam.backend.func.MARKDOWN_FILE_EXTENSION
import dev.triumphteam.backend.func.SUMMARY_FILE_NAME
import dev.triumphteam.backend.func.checksum
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.warn
import dev.triumphteam.markdown.content.ContentRenderer
import dev.triumphteam.markdown.html.MarkdownRenderer
import dev.triumphteam.markdown.summary.SummaryRenderer
import dev.triumphteam.markdown.summary.writer.InvalidSummaryException
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.File
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@OptIn(ExperimentalUnsignedTypes::class)
class Project {

    private val extensions = listOf(
        StrikethroughExtension.create(),
        AutolinkExtension.create(),
        TablesExtension.create(),
        TaskListItemsExtension.create(),
    )

    private val parser = Parser.builder().extensions(extensions).build()
    private val htmlRenderer = HtmlRenderer.builder()
        .nodeRendererFactory(::MarkdownRenderer)
        .extensions(extensions)
        .build()
    private val summaryRenderer = SummaryRenderer()
    private val contentRenderer = ContentRenderer()

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
                    log { "Could not parse summary, invalid syntax!" }
                    rollback()
                    return@transaction
                }

                files.filter { it.name != SUMMARY_FILE_NAME }.forEach pages@{ pageFile ->
                    val content = Pages
                        .select { Pages.project eq projectId }
                        .andWhere { Pages.url eq pageFile.nameWithoutExtension }
                        .firstOrNull()

                    // Doesn't exist, insert
                    if (content == null) {
                        insertPage(projectId, pageFile, projectFile)
                        return@pages
                    }

                    // No change, ignore
                    if (content[Pages.checksum] == pageFile.checksum()) return@pages

                    // Delete existing one
                    Pages.deleteWhere { Pages.id eq content[Pages.id] }
                    insertPage(projectId, pageFile, projectFile)
                }
            }
        }
    }

    private fun insertSummary(projectId: EntityID<Int>, summaryText: String): Boolean {
        val summary = Summaries.select { Summaries.project eq projectId }.firstOrNull()

        val summaryContent = try {
            val document = parser.parse(summaryText)
            document.accept(summaryRenderer)
            summaryRenderer.finalize()
        } catch (ignored: InvalidSummaryException) {
            return false
        }

        // Serializing the data to JSON and storing it on the database is a disgrace
        // But right now it's the fastest solution for me, yikes
        if (summary == null) {
            Summaries.insert {
                it[project] = projectId
                it[content] = JSON.encodeToString(summaryContent)
            }
            return true
        }

        Summaries.update({ Summaries.id eq summary[Summaries.id] }) {
            it[content] = JSON.encodeToString(summaryContent)
        }

        return true
    }

    // TODO attempt batch insert
    private fun insertPage(projectId: EntityID<Int>, pageFile: File, projectFile: File) {
        val markdown = parser.parse(pageFile.readText())

        val pageId = Pages.insertAndGetId {
            it[project] = projectId
            // TODO make sure name doesn't have spaces
            it[url] = pageFile.nameWithoutExtension
            it[content] = htmlRenderer.render(markdown)
            it[github] = githubLink(projectFile.name, pageFile.name)
            it[checksum] = pageFile.checksum()
        }

        val contents = contentRenderer.render(markdown)
            .withIndex()
            .associate { it.index to it.value }

        Contents.batchInsert(contents.entries) { entry ->
            this[page] = pageId
            this[Contents.literal] = entry.value.literal
            this[Contents.href] = entry.value.href
            this[Contents.indent] = entry.value.indent
            this[Contents.position] = (entry.key + 1).toUInt()
        }
    }

    private fun githubLink(project: String, page: String): String {
        val repo = CONFIG[Settings.REPO]
        return """
            https://github.com/${repo.name}/${repo.githubPath}/$project/$page
        """.trimIndent()
    }

    /**
     * Feature companion
     */
    companion object Feature : ApplicationFeature<Application, Project, Project> {
        override val key = AttributeKey<Project>("Project")

        override fun install(pipeline: Application, configure: Project.() -> Unit): Project {
            return Project().apply(configure)
        }
    }

}