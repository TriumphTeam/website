package dev.triumphteam.backend.api

import dev.triumphteam.backend.DATA_FOLDER
import dev.triumphteam.backend.api.database.DocVersionEntity
import dev.triumphteam.backend.api.database.PageEntity
import dev.triumphteam.backend.api.database.ProjectEntity
import dev.triumphteam.backend.banner.BannerMaker
import dev.triumphteam.website.project.Project
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URL
import javax.imageio.ImageIO

private val bannerMaker = BannerMaker()

public fun setupRepository(projects: List<Project>) {
    transaction {
        projects.forEach { project ->

            // Start by deleting project if exists
            // This will cascade down to all other tables
            ProjectEntity.findById(project.id)?.delete()

            val projectEntity = ProjectEntity.new(project.id) {
                this.name = project.name
                this.color = project.color
                this.github = project.projectHome
            }

            val projectIcon = ImageIO.read(URL(""))

            project.versions.forEach { version ->

                val versionEntity = DocVersionEntity.new(version.reference) {
                    this.project = projectEntity
                    this.navigation = version.navigation
                    this.stable = version.stable
                    this.recommended = version.recommended
                }

                val versionFolder = DATA_FOLDER.resolve("${project.id}/${version.reference}").also {
                    it.mkdirs()
                }

                version.pages.forEach { page ->

                    val pageDir = versionFolder.resolve(page.id).also {
                        it.mkdirs()
                    }

                    page.banner.apply {
                        bannerMaker.create(
                            icon = projectIcon,
                            group = group,
                            title = title,
                            subTitle = subTitle,
                            output = pageDir.resolve("banner.png"),
                        )
                    }

                    PageEntity.new(page.id) {
                        this.project = projectEntity
                        this.version = versionEntity
                        this.content = page.content
                        this.summary = page.summary
                    }
                }
            }
        }
    }
}
