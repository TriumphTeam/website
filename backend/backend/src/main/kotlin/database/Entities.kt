package dev.triumphteam.backend.database

import dev.triumphteam.website.JsonSerializer
import dev.triumphteam.website.serializable.PageDocument
import dev.triumphteam.website.serializable.VersionDocument
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
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

public object DocVersions : IdTable<String>("website_project_versions") {
    public override val id: Column<EntityID<String>> = text("version_reference").entityId()
    public val project: Column<EntityID<String>> = reference(
        name = "project_id",
        refColumn = Projects.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_versions_project_id"
    )
    public val versionDocument: Column<VersionDocument> = json<VersionDocument>("document", JsonSerializer.json)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public class DocVersionEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, DocVersionEntity>(DocVersions)

    public var project: ProjectEntity by ProjectEntity referencedOn DocVersions.project
    public var versionDocument: VersionDocument by DocVersions.versionDocument
}

public object Pages : IdTable<String>("website_project_pages") {
    public override val id: Column<EntityID<String>> = text("page_id").entityId()
    public val project: Column<EntityID<String>> = reference(
        name = "project_id",
        refColumn = Projects.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_pages_project_id"
    )
    public val version: Column<EntityID<String>> =
        reference(
            name = "version_reference",
            refColumn = DocVersions.id,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE,
            fkName = "fk_pages_version_reference",
        )
    public val content: Column<PageDocument> = json<PageDocument>("content", JsonSerializer.json)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public class PageEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, PageEntity>(Pages)

    public var project: ProjectEntity by ProjectEntity referencedOn Pages.project
    public var version: DocVersionEntity by DocVersionEntity referencedOn Pages.version
    public var content: PageDocument by Pages.content
}
