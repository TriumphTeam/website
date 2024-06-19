package dev.triumphteam.website.docs

import dev.triumphteam.website.HoconSerializer
import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.api.Api
import dev.triumphteam.website.docs.markdown.MarkdownRenderer
import dev.triumphteam.website.docs.markdown.content.ContentRenderer
import dev.triumphteam.website.docs.markdown.highlight.language.languages.KotlinLanguage
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import dev.triumphteam.website.docs.markdown.highlight.language.languages.GroovyLanguage
import dev.triumphteam.website.docs.markdown.highlight.language.languages.JavaLanguage
import dev.triumphteam.website.docs.markdown.highlight.language.languages.XmlLanguage
import dev.triumphteam.website.docs.markdown.hint.HintExtension
import dev.triumphteam.website.docs.markdown.tab.TabExtension
import dev.triumphteam.website.docs.serialization.GroupConfig
import dev.triumphteam.website.docs.serialization.PageConfig
import dev.triumphteam.website.docs.serialization.ProjectConfig
import dev.triumphteam.website.docs.serialization.RepoSettings
import dev.triumphteam.website.docs.serialization.VersionConfig
import dev.triumphteam.website.project.DocVersion
import dev.triumphteam.website.project.Navigation
import dev.triumphteam.website.project.Page
import dev.triumphteam.website.project.PageSummary
import dev.triumphteam.website.project.Project
import dev.triumphteam.website.project.Repository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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

private val DEFAULT_EXTENSIONS = listOf(
    StrikethroughExtension.create(),
    AutolinkExtension.create(),
    TablesExtension.create(),
    TaskListItemsExtension.create(),
    HintExtension.create(),
    TabExtension.create(),
)

private val htmlRenderer = HtmlRenderer.builder()
    .nodeRendererFactory(::MarkdownRenderer)
    .extensions(DEFAULT_EXTENSIONS)
    .build()

private val contentRenderer = ContentRenderer()

private val mdParser = Parser.builder().extensions(DEFAULT_EXTENSIONS).build()

private val logger: Logger = LoggerFactory.getLogger("docs")

public suspend fun main(args: Array<String>) {

    val code = """
        <dependency test="hello fucker">
            <groupId>dev.triumphteam</groupId>
            <artifactId>triumph-gui</artifactId> <!-- Replace package here here -->
            <version>3.1.7</version>
        </dependency>
    """.trimIndent()

    val language: LanguageDefinition = XmlLanguage

    // val structures = highlights.getCodeStructure().flatten()
    // TODO: FIGURE OUT HOW TO DEAL WITH HTML SYMBOLS
    val highlights = language.captureHighlights(code)
    val injectedCode = buildString {
        code.forEachIndexed { index, char ->
            val structure = highlights[index]
            /*if ((structure?.size ?: 0) > 1) {
                println(structure)
            }*/
            structure?.forEach { append(it.createTag()) }
            append(char)
        }
        highlights.filterKeys { it >= code.length }.forEach { (_, struct) ->
            struct.forEach { append(it.createTag()) }
        }
    }

    println(injectedCode.replace("<<", "&lt;<").replace(">>", ">&gt;"))

    return
    val options = DefaultParser().parse(
        Options().apply {
            addOption(Option.builder("i").longOpt("input").hasArg().required().build())
            addOption(Option.builder("b").longOpt("bearer").hasArg().required().build())
            addOption(Option.builder("u").longOpt("url").hasArg().required().build())
        },
        args,
    )

    // Grab paths to work with
    val inputPath = Path(options.getOptionValue("i")).toFile().also { file ->
        if (!file.isDirectory()) error("Input path is not a valid directory!")
    }

    val bearer = options.getOptionValue("b")
    val url = options.getOptionValue("u")

    logger.info("Starting project parsing!")

    val inputFiles = inputPath.listFiles() ?: emptyArray()

    val repoSettings =
        HoconSerializer.from<RepoSettings>(requireNotNull(inputFiles.find { it.name == "settings.conf" }))

    // Navigate through file structure and parse all projects
    val repo = Repository(
        editPath = repoSettings.editPath,
        projects = inputFiles.mapNotNull { projectDir ->
            // Ignore non-directory files
            if (!projectDir.isDirectory) return@mapNotNull null

            val files = projectDir.listFiles() ?: emptyArray()
            val projectConfig = files.find("project.conf") {
                "Found project folder without a 'project.conf' file, skipping it!"
            } ?: return@mapNotNull null

            val parsedProjectConfig = HoconSerializer.from<ProjectConfig>(projectConfig)

            Project(
                id = parsedProjectConfig.id,
                name = parsedProjectConfig.name,
                icon = parsedProjectConfig.icon,
                projectHome = parsedProjectConfig.projectHome,
                versions = parseVersions(files.filter(File::isDirectory), inputPath),
            ).also {
                logger.info("Parsed project '$${it.id}', with versions: ${it.versions.map(DocVersion::reference)}!")
            }
        }
    )

    logger.info("Paring complete!")
    logger.info("Uploading..")

    // println(JsonSerializer.encode<Repository>(repo))

    val client = HttpClient(CIO) {
        install(Resources)
        install(ContentNegotiation) {
            json(JsonSerializer.json)
        }

        defaultRequest {
            url(url)
            bearerAuth(bearer)
            contentType(ContentType.Application.Json)
        }
    }

    val response = client.post(Api.Setup()) {
        setBody(repo)
    }

    if (response.status != HttpStatusCode.Accepted) {
        error("Could not upload repository to backend!")
    }

    logger.info("Upload complete!")
    client.close()
}

private fun parseVersions(versionDirs: List<File>, parentDir: File): List<DocVersion> {
    return versionDirs.mapNotNull { versionDir ->

        val files = versionDir.listFiles() ?: emptyArray()
        val versionConfig = files.find("version.conf") {
            "Found version folder without a 'version.conf' file, skipping it!"
        } ?: return@mapNotNull null

        val parsedVersionConfig = HoconSerializer.from<VersionConfig>(versionConfig)

        val navigationCollector = NavigationCollector()
        val pageCollector = PageCollector()

        files.filter(File::isDirectory).sortedBy(File::getName).forEach { groupDir ->
            val groupFiles = groupDir.listFiles() ?: emptyArray()
            val groupConfig = groupFiles.find("group.conf") {
                "Found group folder without a 'group.conf' file, skipping it!"
            } ?: return@mapNotNull null

            val parsedGroupConfig = HoconSerializer.from<GroupConfig>(groupConfig)
            navigationCollector.collect(Navigation.Group(parsedGroupConfig.header, parsedGroupConfig.mapPages()))

            val filesMap = groupFiles.associateBy(File::nameWithoutExtension)
            parsedGroupConfig.pages.map(PageConfig::link).forEach { link ->
                val pageFile = requireNotNull(filesMap[link]) {
                    "Could not find file named '$link', make sure the file is created before adding it to the group config."
                }

                if (pageFile.nameWithoutExtension.contains(" ")) {
                    error("Page name cannot contain spaces.")
                }

                val parsedFile = mdParser.parse(pageFile.readText())

                pageCollector.collect(
                    Page(
                        id = pageFile.nameWithoutExtension.lowercase(),
                        content = htmlRenderer.render(parsedFile),
                        summary = PageSummary(
                            path = pageFile.relativeTo(parentDir).path,
                            entries = contentRenderer.render(parsedFile),
                        )
                    )
                )
            }
        }

        DocVersion(
            reference = parsedVersionConfig.reference,
            recommended = parsedVersionConfig.recommended,
            stable = parsedVersionConfig.stable,
            navigation = navigationCollector.collection(),
            pages = pageCollector.collection(),
        )
    }.also { docVersions ->
        require(docVersions.count(DocVersion::recommended) == 1) {
            "Only 1 recommended version is allowed per project."
        }
    }
}

private fun Array<File>.find(fileName: String, log: () -> String): File? {
    val file = find { it.name == fileName }

    if (file == null) {
        logger.warn(log())
        return null
    }

    return file
}
