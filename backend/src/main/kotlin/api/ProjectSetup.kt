package dev.triumphteam.backend.api

import dev.triumphteam.backend.DATA_FOLDER
import dev.triumphteam.backend.content.ContentExtractor
import dev.triumphteam.backend.database.PageEntity
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.database.ProjectEntity
import dev.triumphteam.backend.database.VersionEntity
import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.serializable.ContentSection
import dev.triumphteam.website.serializable.FooterNavigation
import dev.triumphteam.website.serializable.Group
import dev.triumphteam.website.serializable.HeaderComponent
import dev.triumphteam.website.serializable.NavigationGroup
import dev.triumphteam.website.serializable.NavigationPage
import dev.triumphteam.website.serializable.PageContent
import dev.triumphteam.website.serializable.PageDocument
import dev.triumphteam.website.serializable.Repository
import dev.triumphteam.website.serializable.VersionData
import dev.triumphteam.website.serializable.VersionDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
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

                val pages = version.groups.flatMap(Group::pages)

                val versionEntity = VersionEntity.new(id = version.reference) {
                    this.project = projectEntity
                    this.default = version.default
                    this.versionDocument = VersionDocument(
                        versions = project.versions.map {
                            VersionData(it.reference, it.reference == version.reference)
                        },
                        color = project.color,
                        stable = version.stable,
                        groups = version.groups.map { group ->
                            NavigationGroup(
                                name = group.name,
                                pages = group.pages.map { page ->
                                    NavigationPage(id = page.id, name = page.name)
                                },
                            )
                        },
                        platforms = version.platforms,
                        languages = version.languages,
                        buildTools = version.buildTools,
                        github = version.github,
                        discord = version.discord,
                        javadocs = version.javadocs,
                    )
                    this.searchSections = pages.flatMap { page ->
                        ContentExtractor(page.content) { header, content ->
                            ContentSection(page.id, page.name, header.text, header.id, content)
                        }.extract()
                    }
                }

                pages.forEachIndexed { index, page ->

                    val pageId = CompositeID {
                        it[Pages.reference] = page.id
                        it[Pages.project] = projectEntity.id.value
                        it[Pages.version] = versionEntity.id.value
                    }

                    PageEntity.new(id = pageId) {
                        this.content = PageDocument(
                            name = page.name,
                            description = page.description,
                            banner = page.banner,
                            content = page.content,
                            previous = pages.getOrNull(index - 1)?.let { previous ->
                                FooterNavigation(previous.id, previous.name)
                            },
                            next = pages.getOrNull(index + 1)?.let { next ->
                                FooterNavigation(next.id, next.name)
                            },
                            sections = page.content.children.filterIsInstance<HeaderComponent>()
                                .filter { it.level <= 2 }
                                .map { PageContent(it.id, it.text, it.level) },
                        )
                    }
                }
            }
        }
    }

    logger.info("Setup projects done.")
}
