package dev.triumphteam.backend.api

import dev.triumphteam.backend.api.database.DocVersionEntity
import dev.triumphteam.backend.api.database.PageEntity
import dev.triumphteam.backend.api.database.ProjectEntity
import dev.triumphteam.website.project.Project
import org.jetbrains.exposed.sql.transactions.transaction

public fun setupRepository(projects: List<Project>) {
    transaction {
        projects.forEach { project ->

            // Start by deleting project if exists
            // This will cascade down to all other tables
            ProjectEntity.findById(project.id)?.delete()

            val projectEntity = ProjectEntity.new(project.id) {
                this.name = project.name
                this.icon = project.icon
                this.color = project.color
                this.github = project.projectHome
            }

            project.versions.forEach { version ->

                val versionEntity = DocVersionEntity.new(version.reference) {
                    this.project = projectEntity
                    this.navigation = version.navigation
                    this.stable = version.stable
                    this.recommended = version.recommended
                }

                version.pages.forEach { page ->
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
