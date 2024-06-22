package dev.triumphteam.backend.api.database

import dev.triumphteam.website.project.Navigation
import dev.triumphteam.website.project.PageSummary
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

public object Projects : IdTable<String>("projects") {
    public override val id: Column<EntityID<String>> = varchar("project_id", 255).entityId()
    public val name: Column<String> = varchar("name", 255)
    public val icon: Column<String> = varchar("icon", 1024)
    public val color: Column<String> = varchar("color", 10)
    public val github: Column<String> = varchar("github", 1024)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public object DocVersions : IdTable<String>("docs_version") {
    public override val id: Column<EntityID<String>> = varchar("version_id", 255).entityId()
    public val project: Column<EntityID<String>> =
        reference("project_id", Projects, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    public val navigation: Column<Navigation> = serializable<Navigation>("navigation")
    public val stable: Column<Boolean> = bool("stable")
    public val recommended: Column<Boolean> = bool("recommended")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public object Pages : IdTable<String>("pages") {
    public override val id: Column<EntityID<String>> = varchar("page_id", 255).entityId()
    public val project: Column<EntityID<String>> =
        reference("project_id", Projects, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    public val version: Column<EntityID<String>> =
        reference("version_id", DocVersions, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    public val content: Column<String> = text("content")
    public val summary: Column<PageSummary> = serializable<PageSummary>("summary")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public class ProjectEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, ProjectEntity>(Projects)

    public var name: String by Projects.name
    public var icon: String by Projects.icon
    public var color: String by Projects.color
    public var github: String by Projects.github
}

public class DocVersionEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, DocVersionEntity>(DocVersions)

    public var project: ProjectEntity by ProjectEntity referencedOn DocVersions.project
    public var navigation: Navigation by DocVersions.navigation
    public var stable: Boolean by DocVersions.stable
    public var recommended: Boolean by DocVersions.recommended
}

public class PageEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, PageEntity>(Pages)

    public var project: ProjectEntity by ProjectEntity referencedOn Pages.project
    public var version: DocVersionEntity by DocVersionEntity referencedOn Pages.version
    public var content: String by Pages.content
    public var summary: PageSummary by Pages.summary
}
