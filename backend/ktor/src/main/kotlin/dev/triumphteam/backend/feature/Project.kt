package dev.triumphteam.backend.feature

import dev.triumphteam.backend.CONFIG
import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.database.Contents
import dev.triumphteam.backend.database.Contents.page
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.func.JSON
import dev.triumphteam.backend.func.MARKDOWN_FILE_EXTENSION
import dev.triumphteam.backend.func.PROJECT_FILE_NAME
import dev.triumphteam.backend.func.checksum
import dev.triumphteam.backend.func.warn
import dev.triumphteam.backend.project.ProjectData
import dev.triumphteam.markdown.content.ContentRenderer
import dev.triumphteam.markdown.html.MarkdownRenderer
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
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
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
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
    private val contentRenderer = ContentRenderer()

    fun loadAll(repoFolder: File) = CoroutineScope(IO).launch {
        val projects = repoFolder.listFiles()?.filter { it.isDirectory } ?: return@launch
        projects.forEach folder@{ projectTypeFile ->
            projectTypeFile.listFiles()?.filter { it.isDirectory }?.forEach { projectFile ->
                // Gets the current project name
                val projectName = projectFile.name

                val projectData = projectFile.listFiles()?.find { it.name == PROJECT_FILE_NAME }.let {
                    val projectJson = it ?: run {
                        // Removes the project as it's invalid or unavailable
                        transaction {
                            Projects.deleteWhere { Projects.name eq projectName }
                        }

                        warn { "Could not find summary for project ${projectFile.name}, ignoring!" }
                        return@forEach
                    }

                    JSON.decodeFromString<ProjectData>(projectJson.readText())
                }

                val mdFiles = projectFile.listFiles()
                    ?.filter { it.extension == MARKDOWN_FILE_EXTENSION }
                    ?: return@forEach

                transaction {
                    val projectId = Projects.select {
                        Projects.name eq projectName
                    }.firstOrNull()?.get(Projects.id)
                        ?: Projects.insertAndGetId {
                            it[name] = projectName
                            it[type] = when (projectTypeFile.name) {
                                "libraries" -> 1u
                                else -> 0u
                            }

                            val options = projectData.options
                            it[color] = options.color.joinToString(";")
                            it[github] = options.github
                            it[summary] = JSON.encodeToString(projectData.summary)
                        }

                    mdFiles.filter { it.name != PROJECT_FILE_NAME }.forEach pages@{ pageFile ->
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