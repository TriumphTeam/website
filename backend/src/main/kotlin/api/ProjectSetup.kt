package dev.triumphteam.backend.api

import dev.triumphteam.backend.DATA_FOLDER
import dev.triumphteam.backend.api.database.DocVersionEntity
import dev.triumphteam.backend.api.database.PageEntity
import dev.triumphteam.backend.api.database.ProjectEntity
import dev.triumphteam.backend.banner.BannerMaker
import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.project.Repository
import net.lingala.zip4j.ZipFile
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import javax.imageio.ImageIO

private val bannerMaker = BannerMaker()

public fun setupRepository(projects: File) {

    val tempFolder = Files.createTempDirectory("zip-temp").toFile()
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

    transaction {
        repo.projects.forEach { project ->

            // Start by deleting project if exists
            // This will cascade down to all other tables
            ProjectEntity.findById(project.id)?.delete()

            val projectEntity = ProjectEntity.new(project.id) {
                this.name = project.name
                this.color = project.color
                this.github = project.projectHome
            }

            val projectIcon = ImageIO.read(coreDir.resolve("${project.id}/icon.png"))

            project.versions.forEach { version ->

                val versionEntity = DocVersionEntity.new {
                    this.reference = version.reference
                    this.project = projectEntity
                    this.navigation = version.navigation
                    this.stable = version.stable
                    this.recommended = version.recommended
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
}
