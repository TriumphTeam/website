package dev.triumphteam.backend.database

import ContentSection
import PageDocument
import VersionDocument
import dev.triumphteam.website.JsonSerializer
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
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

public object Versions : IntIdTable("website_project_versions") {
    public val reference: Column<String> = text("version_reference")
    public val project: Column<EntityID<String>> = reference(
        name = "project_id",
        refColumn = Projects.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_versions_project_id"
    )
    public val versionDocument: Column<VersionDocument> = json<VersionDocument>("document", JsonSerializer.json)
    public val searchSections: Column<List<ContentSection>> =
        json<List<ContentSection>>("search-sections", JsonSerializer.json)
    public val default: Column<Boolean> = bool("default").default(false)
}

public class VersionEntity(id: EntityID<Int>) : IntEntity(id) {
    public companion object : IntEntityClass<VersionEntity>(Versions)

    public var reference: String by Versions.reference
    public var project: ProjectEntity by ProjectEntity referencedOn Versions.project
    public var versionDocument: VersionDocument by Versions.versionDocument
    public var searchSections: List<ContentSection> by Versions.searchSections
    public var default: Boolean by Versions.default
}

public object Pages : IntIdTable("website_project_pages") {
    public val reference: Column<String> = text("page_reference")
    public val version: Column<EntityID<Int>> =
        reference(
            name = "version_id",
            refColumn = Versions.id,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE,
            fkName = "fk_pages_version_id",
        )
    public val content: Column<PageDocument> = json<PageDocument>("content", JsonSerializer.json)
}

public class PageEntity(id: EntityID<Int>) : IntEntity(id) {
    public companion object : IntEntityClass<PageEntity>(Pages)

    public var reference: String by Pages.reference
    public var version: VersionEntity by VersionEntity referencedOn Pages.version
    public var content: PageDocument by Pages.content
}
