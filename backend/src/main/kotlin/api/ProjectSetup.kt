package dev.triumphteam.backend.api

import dev.triumphteam.backend.DATA_FOLDER
import dev.triumphteam.backend.api.database.DocVersionEntity
import dev.triumphteam.backend.api.database.PageEntity
import dev.triumphteam.backend.api.database.ProjectEntity
import dev.triumphteam.backend.banner.BannerMaker
import dev.triumphteam.backend.meilisearch.Meili
import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.project.Page
import dev.triumphteam.website.project.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.lingala.zip4j.ZipFile
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import javax.imageio.ImageIO

private val logger = LoggerFactory.getLogger("project-setup")
private val bannerMaker = BannerMaker()

public suspend fun setupRepository(meili: Meili, projects: File) {

    logger.info("Setup projects request received.")

    val tempFolder = withContext(Dispatchers.IO) {
        Files.createTempDirectory("zip-temp")
    }.toFile()

    ZipFile(projects).extractAll(tempFolder.path)

    val json = tempFolder.resolve("repository.json")
    if (!json.exists()) {
        throw FileNotFoundException("Could not find temporary repository at ${tempFolder.path}")
    }

    val coreDir = DATA_FOLDER.resolve("core").also(File::mkdirs)
    // Copy files
    tempFolder.listFiles()?.filter(File::isDirectory)?.forEach {
        it.copyRecursively(coreDir.resolve(it.name), overwrite = true)
    }
    // Parse repos
    val repo = JsonSerializer.from<Repository>(json)

    // Delete downloaded files
    projects.delete()

    logger.info("Inserting projects...")
    transaction {
        repo.projects.forEach { project ->

            // Start by deleting project if exists
            // This will cascade down to all other tables
            ProjectEntity.findById(project.id)?.delete()

            val projectEntity = ProjectEntity.new(project.id) {
                this.name = project.name
                this.color = project.color
                this.github = project.projectHome
                this.discord = project.discord
            }

            val projectIcon = ImageIO.read(coreDir.resolve("${project.id}/icon.png"))

            project.versions.forEach { version ->

                val versionEntity = DocVersionEntity.new {
                    this.reference = version.reference
                    this.project = projectEntity
                    this.navigation = version.navigation
                    this.stable = version.stable
                    this.recommended = version.recommended
                    this.defaultPage = version.pages.find { it.default }?.id ?: error("Could not find default page.")
                    this.github = version.github
                    this.discord = version.discord
                    this.javadocs = version.javadocs
                }

                val versionFolder = DATA_FOLDER.resolve("core/${project.id}/${version.reference}").also {
                    it.mkdirs()
                }

                version.pages.forEach { page ->

                    val pageDir = versionFolder.resolve(page.id).also {
                        it.mkdirs()
                    }

                    val description = page.description

                    bannerMaker.create(
                        icon = projectIcon,
                        group = description.group,
                        title = description.title,
                        subTitle = description.subTitle,
                        output = pageDir.resolve("banner.png"),
                    )

                    PageEntity.new {
                        this.pageId = page.id
                        this.project = projectEntity
                        this.version = versionEntity
                        this.content = page.content
                        this.path = page.path
                        this.title = description.title ?: ""
                        this.subTitle = description.subTitle ?: ""
                        this.summary = description.summary
                    }
                }
            }
        }
    }

    logger.info("Preparing search...")
    // Search setup
    repo.projects.forEach { project ->
        project.versions.forEach { version ->

            val projectId = projectIndex(project.id, version.reference)

            // First delete it all
            meili.client.index(projectId).delete()

            // Then re-add new stuff
            meili.client.index(projectId, primaryKey = "id").addDocuments(
                version.pages.flatMap { page ->
                    listOf(descriptionDocument(page.id, page.description))
                        .plus(
                            page.description.summary.map { summary ->
                                SearchDocument(
                                    id = SearchDocument.createId(page.id, summary.href),
                                    pageId = page.id,
                                    anchor = summary.href,
                                    isAnchor = true,
                                    reference = summary.terms,
                                )
                            }
                        )
                }
            )
        }
    }

    logger.info("Setup projects done.")
}

private fun descriptionDocument(id: String, description: Page.Description): SearchDocument {
    return SearchDocument(
        id = id,
        pageId = id,
        anchor = id,
        isAnchor = false,
        reference = listOfNotNull(description.title, description.subTitle),
    )
}

@Serializable
public data class SearchDocument(
    public val id: String,
    public val pageId: String,
    public val anchor: String,
    public val isAnchor: Boolean,
    public val reference: List<String>,
) {

    public companion object {

        public fun createId(page: String, id: String): String {
            return "$page-$id"
        }
    }
}

public fun projectIndex(project: String, version: String): String = "$project-${version.replace(".", "_")}"
