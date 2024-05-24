package dev.triumphteam.backend.feature

import dev.triumphteam.backend.func.HOCON
import dev.triumphteam.backend.func.warn
import dev.triumphteam.markdown.content.ContentRenderer
import dev.triumphteam.markdown.hint.HintExtension
import dev.triumphteam.markdown.tab.TabExtension
import io.ktor.server.application.Application
import io.ktor.server.application.Plugin
import io.ktor.server.application.plugin
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import java.io.File

class Project(application: Application) {

    companion object Feature : Plugin<Application, Project, Project> {

        private const val PROJECT_CONF_FILE = "project.conf"

        override val key = AttributeKey<Project>("Project")

        override fun install(pipeline: Application, configure: Project.() -> Unit): Project {
            return Project(pipeline).apply(configure)
        }
    }

    private val git = application.plugin(Github)

    private val scope = CoroutineScope(IO)
    private val extensions = listOf(
        StrikethroughExtension.create(),
        AutolinkExtension.create(),
        TablesExtension.create(),
        TaskListItemsExtension.create(),
        HintExtension(),
        TabExtension(),
    )

    private val parser = Parser.builder().extensions(extensions).build()
    // TODO change to json
    /*private val htmlRenderer = HtmlRenderer.builder()
        .nodeRendererFactory(::MarkdownRenderer)
        .extensions(extensions)
        .build()*/

    private val contentRenderer = ContentRenderer()

    fun loadAll(repoFolder: File) {
        scope.launch {

            val projects = repoFolder.listFiles()?.filter { it.isDirectory }
            requireNotNull(projects) {
                "Could not find projects in the repo files!"
            }

            projects.forEach { projectDir ->
                val projectFiles = projectDir.listFiles() ?: emptyArray()

                val projectConf = projectFiles.find { it.name == PROJECT_CONF_FILE }?.let {
                    HOCON.decode
                } ?: run {
                    warn { "Found folder that does not contain '$PROJECT_CONF_FILE', ignoring it." }
                    return@forEach
                }


            }

            /*val projects = repoFolder.listFiles()?.filter { it.isDirectory } ?: return@launch
            projects.forEach folder@{ projectTypeFile ->
                val projectType = projectTypeFile.name.toSingularProjectType() ?: return@folder

                projectTypeFile.listFiles()?.filter { it.isDirectory }?.forEach { projectFolder ->
                    // Gets the current project name
                    val projectId = projectFolder.name

                    val projectData = projectFolder.listFiles()?.find { it.name == PROJECT_FILE_NAME }.let {
                        val projectJson = it ?: run {
                            // Removes the project as it's invalid or unavailable
                            transaction {
                                Projects.deleteWhere { Projects.id eq projectId }
                            }

                            warn { "Could not find summary for project ${projectFolder.name}, ignoring!" }
                            return@forEach
                        }

                        JSON.decodeFromString<ProjectData>(projectJson.readText())
                    }

                    val hasIcon = projectFolder.listFiles()?.any { it.name == PROJECT_ICON_NAME } ?: false

                    if (!hasIcon) {
                        warn { "Could not find icon for project ${projectFolder.name}, ignoring!" }
                        return@forEach
                    }

                    val mdFiles = projectFolder.listFiles()
                        ?.filter { it.extension == MARKDOWN_FILE_EXTENSION }
                        ?: return@forEach

                    transaction {
                        val id = Projects.replace {
                            val options = projectData.options
                            val repo = CONFIG[Settings.REPO]

                            it[Projects.id] = projectId
                            it[name] = options.name
                            it[icon] =
                                "https://github.com/${repo.name}/raw/main/${projectTypeFile.name}/${projectFolder.name}/${PROJECT_ICON_NAME}"
                            it[type] = projectType.projectType

                            val release = runBlocking {
                                git.getRelease(options.github)
                            }

                            it[version] = release?.version ?: "Soon"
                            it[color] = options.color.joinToString(";")
                            it[github] = options.github
                            it[summary] = JSON.encodeToString(projectData.summary)
                        }[Projects.id]

                        mdFiles.filter { it.name != PROJECT_FILE_NAME }.forEach pages@{ pageFile ->
                            val content = Pages
                                .select { Pages.project eq id }
                                .andWhere { Pages.url eq pageFile.nameWithoutExtension }
                                .firstOrNull()

                            // Doesn't exist, insert
                            if (content == null) {
                                insertPage(id, pageFile, projectFolder, projectTypeFile.name)
                                return@pages
                            }

                            // No change, ignore
                            if (content[Pages.checksum] == pageFile.checksum()) return@pages

                            // Delete existing one
                            Pages.deleteWhere { Pages.id eq content[Pages.id] }
                            insertPage(id, pageFile, projectFolder, projectTypeFile.name)
                        }
                    }
                }
            }*/
        }
    }

    // TODO attempt batch insert
    private fun insertPage(projectId: String, pageFile: File, projectFile: File, typeName: String) {
        /*val markdown = parser.parse(pageFile.readText())

        val pageId = Pages.insertAndGetId {
            it[project] = projectId
            // TODO make sure name doesn't have spaces
            it[url] = pageFile.nameWithoutExtension
            it[content] = htmlRenderer.render(markdown)
            it[github] = githubLink(typeName, projectFile.name, pageFile.name)
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
        }*/
    }

    /*private fun githubLink(typeName: String, project: String, page: String): String {
        val repo = CONFIG[Settings.REPO]
        return """
            https://github.com/${repo.name}/${repo.githubPath}/${typeName}/$project/$page
        """.trimIndent()
    }*/
}
