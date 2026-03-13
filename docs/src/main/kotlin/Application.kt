package dev.triumphteam.website.docs

import dev.triumphteam.website.HoconSerializer
import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.api.InternalApi
import dev.triumphteam.website.docs.markdown.MarkdownRenderer
import dev.triumphteam.website.docs.markdown.hint.HintExtension
import dev.triumphteam.website.docs.markdown.placeholder.PlaceholderExtension
import dev.triumphteam.website.docs.markdown.tab.TabExtension
import dev.triumphteam.website.docs.project.GroupConfig
import dev.triumphteam.website.docs.project.PageConfig
import dev.triumphteam.website.docs.project.ProjectConfig
import dev.triumphteam.website.docs.project.Replacement
import dev.triumphteam.website.docs.project.RepoSettings
import dev.triumphteam.website.docs.project.VersionConfig
import dev.triumphteam.website.serializable.Group
import dev.triumphteam.website.serializable.Page
import dev.triumphteam.website.serializable.Project
import dev.triumphteam.website.serializable.Repository
import dev.triumphteam.website.serializable.Version
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import net.lingala.zip4j.ZipFile
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.io.path.Path

private const val OUTPUT_FILE_NAME = "repository.json"
private const val SETTINGS_CONFIG_FILE_NAME = "settings.conf"
private const val PROJECT_CONFIG_FILE_NAME = "project.conf"
private const val VERSION_CONFIG_FILE_NAME = "version.conf"
private const val GROUP_CONFIG_FILE_NAME = "group.conf"
private const val PAGE_CONFIG_FILE_NAME = "page.conf"
private const val ICON_FILE_NAME = "icon.png"
private const val PAGE_FILE_NAME = "index.md"
private const val MD_FILE_EXTENSION = "md"
private const val HOCON_FILE_EXTENSION = "conf"
private const val ASSETS_DIRECTORY = "assets"

private val DEFAULT_EXTENSIONS = listOf(
    StrikethroughExtension.create(),
    AutolinkExtension.create(),
    TablesExtension.create(),
    TaskListItemsExtension.create(),
    HintExtension.create(),
    TabExtension.create(),
    PlaceholderExtension.create(),
)

public val MARKDOWN_PARSER: Parser = Parser.builder()
    .extensions(DEFAULT_EXTENSIONS)
    .build()

private val logger: Logger = LoggerFactory.getLogger("docs")

public suspend fun main(args: Array<String>) {

    val environmentPath = System.getenv("INPUT_DIR")

    val options = DefaultParser().parse(
        Options().apply {
            addOption(Option.builder("i").longOpt("input").hasArg().build())
            //addOption(Option.builder("b").longOpt("bearer").hasArg().required().build())
            //addOption(Option.builder("u").longOpt("url").hasArg().required().build())
        },
        args,
    )

    // Grab paths to work with
    val inputPath = when {
        environmentPath != null -> Path(environmentPath)
        else -> Path(
            requireNotNull(options.getOptionValue("i")) {
                "Could not find input path! Please provide it with the -i flag!"
            },
        )
    }.toFile()

    require(inputPath.exists() && inputPath.isDirectory) {
        "Input path '${inputPath.absolutePath}' does not exist or is not a directory!"
    }

    logger.info("Using input path: ${inputPath.absolutePath}")

    val bearer = "bearer" //options.getOptionValue("b")
    val url = "http://127.0.0.1:8001" //options.getOptionValue("u")

    logger.info("Starting project parsing!")

    val inputFiles = inputPath.listFiles() ?: emptyArray()

    val repoSettings = HoconSerializer.from<RepoSettings>(
        inputFiles.findFile(SETTINGS_CONFIG_FILE_NAME) {
            "Found repository without a '$SETTINGS_CONFIG_FILE_NAME' file!"
        },
    )

    // Navigate through the file structure and parse all projects
    val projects = Projects(
        projects = inputFiles.filter(File::isDirectory).filterNot { it.name == ASSETS_DIRECTORY }.map { projectDir ->

            val files = projectDir.listFiles() ?: emptyArray()
            val projectConfig = files.findFile(PROJECT_CONFIG_FILE_NAME) {
                "Found project folder without a '$PROJECT_CONFIG_FILE_NAME' file"
            }.let { HoconSerializer.from<ProjectConfig>(it) }

            ProjectWithIcon(
                project = Project(
                    id = projectConfig.id,
                    name = projectConfig.name,
                    color = projectConfig.color,
                    versions = parseVersions(files.filter(File::isDirectory), inputPath, repoSettings),
                ),
                icon = files.findFile(ICON_FILE_NAME) {
                    "Found project folder without an '$ICON_FILE_NAME'. Please make sure to add an icon for the project!"
                },
            ).also {
                logger.info("Parsed project '${it.project.id}', with versions: ${it.project.versions.map(Version::reference)}!")
            }
        },
    )

    val assets = inputPath.resolve(ASSETS_DIRECTORY).takeIf(File::exists)?.listFiles() ?: emptyArray()

    val outputDir = File("output").also(File::mkdirs)

    val repository = outputDir.resolve(OUTPUT_FILE_NAME).also {
        it.writeText(JsonSerializer.encode<Repository>(projects.toRepository()))
    }

    val icons = projects.projects.map {
        outputDir.resolve(it.project.id).also { dir ->
            dir.mkdirs()
            it.icon.copyTo(dir.resolve(ICON_FILE_NAME), overwrite = true)
        }
    }

    val outputAssetsDir = outputDir.resolve("static").also { it.mkdirs() }
    assets.forEach { it.copyTo(outputAssetsDir.resolve(it.name), overwrite = true) }

    val zip = ZipFile("projects.zip").also {
        icons.forEach { dir ->
            it.addFolder(dir)
        }
        it.addFile(repository)
        it.addFolder(outputAssetsDir)
    }

    logger.info("Parsing complete!")
    logger.info("Uploading..")

    val client = HttpClient(CIO) {
        install(Resources)
        install(ContentNegotiation) {
            json(JsonSerializer.json)
        }

        defaultRequest {
            url(url)
            bearerAuth(bearer)
        }
    }

    val response = client.post(InternalApi.Setup()) {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append(
                        "zip", zip.file.readBytes(),
                        Headers.build {
                            append(HttpHeaders.ContentType, "multipart/form-data")
                            append(HttpHeaders.ContentDisposition, "filename=\"projects.zip\"")
                        },
                    )
                },
                boundary = "WebAppBoundary",
            ),
        )
        onUpload { bytesSentTotal, contentLength ->
            logger.info("Sent $bytesSentTotal bytes from $contentLength")
        }
    }

    if (response.status != HttpStatusCode.Accepted) {
        error("Could not upload repository to backend '${response.status}'!")
    }

    logger.info("Upload complete!")
    client.close()
}

private fun parseVersions(versions: List<File>, rootDir: File, repoSettings: RepoSettings): List<Version> {
    return versions.map { versionDir ->
        val files = versionDir.listFiles() ?: emptyArray()
        val versionConfig = files.findFile(VERSION_CONFIG_FILE_NAME) {
            "Found version folder without a '$VERSION_CONFIG_FILE_NAME' file!"
        }.let { HoconSerializer.from<VersionConfig>(it) }

        // Only directories are allowed at this stage, since we want to look into groups.
        // No pages are allowed outside a group.
        val groups = files.filter(File::isDirectory).map { groupDir ->
            val groupFiles = groupDir.listFiles() ?: emptyArray()

            val groupConfig = groupFiles.findFile(GROUP_CONFIG_FILE_NAME) {
                "Found group folder without a '$GROUP_CONFIG_FILE_NAME' file!"
            }.let { HoconSerializer.from<GroupConfig>(it) }

            // The first level of a group is also only folders.
            val pages = groupFiles.filter(File::isDirectory).map { pageDir ->
                val pageFiles = pageDir.listFiles() ?: emptyArray()

                // The configuration of the page, like name, id, order, etc.
                val pageConfig = pageFiles.findFile(PAGE_CONFIG_FILE_NAME) {
                    "Found page folder without a '$PAGE_CONFIG_FILE_NAME' file! ${pageDir.name} is the problem."
                }.let { HoconSerializer.from<PageConfig>(it) }

                // The actual Markdown page file.
                val pageFile = pageFiles.findFile(PAGE_FILE_NAME) {
                    "Found page directory without a '$PAGE_FILE_NAME' file, skipping it!"
                }

                // Map replacement.
                // Walk top down on child folders to collect all files.
                val replacements = pageDir.walkTopDown()
                    .filterNot { it.name == PAGE_FILE_NAME || it.name == PAGE_CONFIG_FILE_NAME } // Remove the main files from the list.
                    .filter(File::isFile)
                    .associate { replacement ->
                        replacement.nameWithoutExtension to when (replacement.extension) {
                            MD_FILE_EXTENSION -> Replacement.Raw(replacement.readText())
                            HOCON_FILE_EXTENSION -> HoconSerializer.from<Replacement.Conditional>(replacement)
                            else -> error("Unsupported file extension '${replacement.extension}'!")
                        }
                    }

                Page(
                    id = pageConfig.id,
                    path = "${repoSettings.editPath.removeSuffix("/")}/${pageDir.relativeTo(rootDir).invariantSeparatorsPath}",
                    name = pageConfig.name,
                    description = pageConfig.description,
                    banner = pageConfig.banner,
                    order = pageConfig.order,
                    content = MarkdownRenderer(replacements).render(MARKDOWN_PARSER.parse(pageFile.readText())),
                )
            }.sortedBy(Page::order)

            Group(
                name = groupConfig.name,
                order = groupConfig.order,
                pages = pages,
            )
        }.sortedBy(Group::order)

        Version(
            reference = versionConfig.reference,
            stable = versionConfig.stable,
            default = versionConfig.default,
            settings = versionConfig.settings,
            github = versionConfig.github,
            discord = versionConfig.discord,
            javadocs = versionConfig.javadocs,
            groups = groups,
        )
    }.also { docVersions ->
        if (docVersions.isEmpty()) {
            logger.warn("No versions found for project '${rootDir.name}'.")
            return@also
        }

        require(docVersions.count { it.default } == 1) {
            "Project '${rootDir.name}' should only have one default version, but found ${docVersions.count { it.default }}!"
        }
    }
}

private fun Array<File>.findFile(fileName: String, log: () -> String): File {
    return find { it.name == fileName } ?: error(log())
}

private data class ProjectWithIcon(val project: Project, val icon: File)

private data class Projects(val projects: List<ProjectWithIcon>) {

    fun toRepository(): Repository {
        return Repository(projects.map(ProjectWithIcon::project))
    }
}
