package dev.triumphteam.backend.database

import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.serializable.ContentSection
import dev.triumphteam.website.serializable.PageDocument
import dev.triumphteam.website.serializable.VersionDocument
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.json.json

public object Projects : IdTable<String>("website_projects") {
    public override val id: Column<EntityID<String>> = text("project_id").entityId()
    public val name: Column<String> = text("name")
    public val color: Column<String> = text("color")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public class ProjectEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, ProjectEntity>(Projects)

    public var name: String by Projects.name
    public var color: String by Projects.color
}

public object Versions : IdTable<String>("website_project_versions") {
    public override val id: Column<EntityID<String>> = text("version_reference").entityId()
    public val project: Column<EntityID<String>> = reference(
        name = "project_id",
        refColumn = Projects.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_versions_project_id",
    )
    public val versionDocument: Column<VersionDocument> = json<VersionDocument>("document", JsonSerializer.json)
    public val searchSections: Column<List<ContentSection>> =
        json<List<ContentSection>>("search-sections", JsonSerializer.json)
    public val default: Column<Boolean> = bool("default").default(false)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public class VersionEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, VersionEntity>(Versions)

    public var project: ProjectEntity by ProjectEntity referencedOn Versions.project
    public var versionDocument: VersionDocument by Versions.versionDocument
    public var searchSections: List<ContentSection> by Versions.searchSections
    public var default: Boolean by Versions.default
}

public object Pages : CompositeIdTable("website_project_pages") {
    public val reference: Column<EntityID<String>> = text("page_reference").entityId()
    public val project: Column<EntityID<String>> = reference(
        name = "project_id",
        refColumn = Projects.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_pages_project_id",
    )
    public val version: Column<EntityID<String>> =
        reference(
            name = "version_id",
            refColumn = Versions.id,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE,
            fkName = "fk_pages_version_id",
        )
    public val content: Column<PageDocument> = json<PageDocument>("content", JsonSerializer.json)

    init {
        addIdColumn(project)
        addIdColumn(version)
    }

    override val primaryKey: PrimaryKey = PrimaryKey(reference, project, version)
}

public class PageEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    public companion object : CompositeEntityClass<PageEntity>(Pages)

    public var content: PageDocument by Pages.content
}
