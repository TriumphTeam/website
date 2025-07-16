package dev.triumphteam.backend.api

import dev.triumphteam.backend.DATA_FOLDER
import dev.triumphteam.backend.database.DocVersionEntity
import dev.triumphteam.backend.database.PageEntity
import dev.triumphteam.backend.database.ProjectEntity
import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.serializable.DocComponent
import dev.triumphteam.website.serializable.Group
import dev.triumphteam.website.serializable.PageDocument
import dev.triumphteam.website.serializable.Repository
import dev.triumphteam.website.serializable.VersionDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.lingala.zip4j.ZipFile
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

private val logger = LoggerFactory.getLogger("project-setup")

public suspend fun setupRepository(projects: File) {

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

            // Start by deleting the project if exists.
            // This will cascade down to all other tables.
            ProjectEntity.findById(project.id)?.delete()

            val projectEntity = ProjectEntity.new(id = project.id) {
                this.name = project.name
                this.color = project.color
            }

            project.versions.forEach { version ->

                val versionEntity = DocVersionEntity.new(id = version.reference) {
                    this.project = projectEntity
                    this.versionDocument = VersionDocument(
                        versions = project.versions.map { version ->
                            VersionDocument.Version(version.reference)
                        },
                        color = project.color,
                        stable = version.stable,
                        groups = version.groups.map { group ->
                            VersionDocument.Group(
                                name = group.name,
                                pages = group.pages.map { page ->
                                    VersionDocument.Page(id = page.id, name = page.name)
                                }
                            )
                        },
                        platforms = version.platforms,
                        languages = version.languages,
                        buildTools = version.buildTools,
                        github = version.github,
                        discord = version.discord,
                        javadocs = version.javadocs,
                    )
                }

                val pages = version.groups.flatMap(Group::pages)
                pages.forEachIndexed { index, page ->

                    PageEntity.new(id = page.id) {
                        this.project = projectEntity
                        this.version = versionEntity
                        this.content = PageDocument(
                            name = page.name,
                            description = page.description,
                            content = page.content,
                            previous = pages.getOrNull(index - 1)?.let { previous ->
                                PageDocument.Navigation(previous.id, previous.name)
                            },
                            next = pages.getOrNull(index + 1)?.let { next ->
                                PageDocument.Navigation(next.id, next.name)
                            },
                            sections = page.content.children.filterIsInstance<DocComponent.Header>()
                                .filter { it.level <= 2 }
                                .map { PageDocument.PageContent(it.id, it.text, it.level) },
                        )
                    }
                }
            }
        }
    }

    logger.info("Setup projects done.")
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
