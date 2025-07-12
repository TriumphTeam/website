package dev.triumphteam.website.docs

import dev.triumphteam.website.HoconSerializer
import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.api.Api
import dev.triumphteam.website.docs.markdown.MarkdownRenderer
import dev.triumphteam.website.docs.markdown.hint.HintExtension
import dev.triumphteam.website.docs.markdown.placeholder.PlaceholderExtension
import dev.triumphteam.website.docs.markdown.tab.TabExtension
import dev.triumphteam.website.docs.project.Replacement
import dev.triumphteam.website.docs.serialization.ProjectConfig
import dev.triumphteam.website.docs.serialization.RepoSettings
import dev.triumphteam.website.docs.serialization.VersionConfig
import dev.triumphteam.website.project.DocVersion
import dev.triumphteam.website.project.Project
import dev.triumphteam.website.project.Repository
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
import org.commonmark.renderer.html.HtmlRenderer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.io.path.Path

private const val OUTPUT_FILE_NAME = "repository.json"
private const val VERSION_FILE_NAME = "version.conf"
private const val PROJECT_FILE_NAME = "project.conf"
private const val SETTINGS_FILE_NAME = "settings.conf"
private const val ICON_FILE_NAME = "icon.png"
private const val PAGE_FILE_NAME = "index.md"
private const val MD_FILE_EXTENSION = "md"
private const val HOCON_FILE_EXTENSION = "conf"

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

    val options = DefaultParser().parse(
        Options().apply {
            addOption(Option.builder("i").longOpt("input").hasArg().required().build())
            //addOption(Option.builder("b").longOpt("bearer").hasArg().required().build())
            //addOption(Option.builder("u").longOpt("url").hasArg().required().build())
        },
        args,
    )

    // Grab paths to work with
    val inputPath = Path(options.getOptionValue("i")).toFile().also { file ->
        if (!file.isDirectory()) error("Input path is not a valid directory!")
    }

    val bearer = "bearer" //options.getOptionValue("b")
    val url = "" //options.getOptionValue("u")

    logger.info("Starting project parsing!")

    val inputFiles = inputPath.listFiles() ?: emptyArray()

    val repoSettings =
        HoconSerializer.from<RepoSettings>(requireNotNull(inputFiles.find { it.name == SETTINGS_FILE_NAME }))

    // Navigate through the file structure and parse all projects
    val projects = Projects(
        projects = inputFiles.mapNotNull { projectDir ->
            // Ignore non-directory files
            if (!projectDir.isDirectory) return@mapNotNull null

            val files = projectDir.listFiles() ?: emptyArray()
            val projectConfig = files.findFile(PROJECT_FILE_NAME) {
                "Found project folder without a '$PROJECT_FILE_NAME' file, skipping it!"
            } ?: return@mapNotNull null

            val parsedProjectConfig = HoconSerializer.from<ProjectConfig>(projectConfig)

            ProjectWithIcon(
                project = Project(
                    id = parsedProjectConfig.id,
                    name = parsedProjectConfig.name,
                    color = parsedProjectConfig.color,
                    projectHome = parsedProjectConfig.projectHome,
                    versions = parseVersions(files.filter(File::isDirectory), projectDir, repoSettings),
                    discord = parsedProjectConfig.discord,
                ),
                icon = requireNotNull(files.findFile(ICON_FILE_NAME)) {
                    "Found project folder without an '$ICON_FILE_NAME'. Please make sure to add an icon for the project!"
                }
            ).also {
                logger.info("Parsed project '${it.project.id}', with versions: ${it.project.versions.map(DocVersion::reference)}!")
            }
        },
    )

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

    val zip = ZipFile("projects.zip").also {
        icons.forEach { dir ->
            it.addFolder(dir)
        }
        it.addFile(repository)
    }

    logger.info("Parsing complete!")
    println(projects)
    return
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

    val response = client.post(Api.Setup()) {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("zip", zip.file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "multipart/form-data")
                        append(HttpHeaders.ContentDisposition, "filename=\"projects.zip\"")
                    })
                },
                boundary = "WebAppBoundary"
            )
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

private fun parseVersions(versions: List<File>, projectDir: File, repoSettings: RepoSettings): List<DocVersion> {
    return versions.mapNotNull { versionDir ->

        val files = versionDir.listFiles() ?: emptyArray()
        val versionConfig = files.findFile(VERSION_FILE_NAME) {
            "Found version folder without a '$VERSION_FILE_NAME' file, skipping it!"
        } ?: return@mapNotNull null

        val parsedVersionConfig = HoconSerializer.from<VersionConfig>(versionConfig)

        val navigationCollector = NavigationCollector()
        val pageCollector = PageCollector()

        // Only directories are allowed at this stage, since we want to look into groups.
        // No page is allowed outside a group.
        files.filter(File::isDirectory).sortedBy(File::getName).forEach { groupDir ->
            // Within a group, we also sort by name.
            // The file can be a single file, or a more complex mix of files within a folder.
            (groupDir.listFiles()?.sortedBy(File::getName) ?: emptyList()).forEach { file ->

                // If it's just a file, handle it.
                if (!file.isDirectory) {
                    require(file.extension == MD_FILE_EXTENSION) {
                        "Only markdown files are allowed within groups, '${file.name}' is not a valid file!"
                    }
                    // Handle simple parsing
                    return@forEach
                }

                val pageFiles = file.listFiles() ?: emptyArray()
                val pageFile = pageFiles.findFile(PAGE_FILE_NAME) {
                    "Found page directory without a '$PAGE_FILE_NAME' file, skipping it!"
                } ?: return@forEach

                // Map replacement.
                val replacements = pageFiles.filter { it.name != pageFile.name }.associate { replacement ->
                    replacement.nameWithoutExtension to when (replacement.extension) {
                        MD_FILE_EXTENSION -> Replacement.Markdown(replacement.readText())
                        HOCON_FILE_EXTENSION -> TODO("Not yet implemented")
                        else -> error("Unsupported file extension '${replacement.extension}'!")
                    }
                }

                val pageNode = MARKDOWN_PARSER.parse(pageFile.readText())
                val page = MarkdownRenderer(replacements).render(pageNode)
                println(page)
                println(replacements)

                println(projectDir.name) // Project
                println(versionDir.name) // Version
                println(groupDir.name) // Group
                println(file.name) // File

                Unit
            }

            // Group config parsed
            // Figure out how to no need the "navigation collector" bs

            println(navigationCollector.collection())

            /*val filesMap = groupFiles.associateBy(File::nameWithoutExtension)
            parsedGroupConfig.pages.forEach { page ->
                val pageFile = requireNotNull(filesMap[page.link]) {
                    "Could not find file named '${page.link}', make sure the file is created before adding it to the group config."
                }

                if (pageFile.nameWithoutExtension.contains(" ")) {
                    error("Page name cannot contain spaces.")
                }

                val parsedFile = mdParser.parse(pageFile.readText())
                val summaryExtractor = SummaryExtractor()

                val (title, subTitle) = PageDescriptionExtractor().extract(parsedFile)

                pageCollector.collect(
                    Page(
                        id = pageFile.nameWithoutExtension.lowercase(),
                        content = htmlRenderer.render(parsedFile),
                        path = "${repoSettings.editPath.removeSuffix("/")}/${pageFile.relativeTo(parentDir).path}",
                        description = Page.Description(
                            title = title,
                            subTitle = subTitle?.trimAround(contextLength = 100),
                            group = parsedGroupConfig.header,
                            summary = summaryExtractor.extract(parsedFile),
                        ),
                        default = page.default,
                    )
                )
            }*/
        }

        DocVersion(
            reference = parsedVersionConfig.reference,
            recommended = parsedVersionConfig.recommended,
            stable = parsedVersionConfig.stable,
            navigation = navigationCollector.collection(),
            pages = pageCollector.collection().also { pages ->
                require(pages.count { it.default } == 1) {
                    "Versions must have 1 and only 1 default page."
                }
            },
            github = parsedVersionConfig.github,
            discord = parsedVersionConfig.discord,
            javadocs = parsedVersionConfig.javadocs,
        )
    }.also { docVersions ->
        if (docVersions.isEmpty()) {
            logger.warn("No versions found for project '${projectDir.name}'.")
            return@also
        }

        require(docVersions.count(DocVersion::recommended) == 1) {
            "Only 1 recommended version is allowed per project."
        }
    }
}

private fun Array<File>.findFile(fileName: String, log: (() -> String)? = null): File? {
    val file = find { it.name == fileName }

    if (file == null) {
        log?.invoke()?.let { logger.warn(it) }
        return null
    }

    return file
}

private data class ProjectWithIcon(val project: Project, val icon: File)

private data class Projects(val projects: List<ProjectWithIcon>) {

    fun toRepository(): Repository {
        return Repository(projects.map(ProjectWithIcon::project))
    }
}
