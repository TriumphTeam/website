package dev.triumphteam.backend.api.database

import dev.triumphteam.website.project.Navigation
import dev.triumphteam.website.project.PageSummary
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

public object Projects : IdTable<String>("projects") {
    public override val id: Column<EntityID<String>> = varchar("project_id", 255).entityId()
    public val name: Column<String> = varchar("name", 255)
    public val color: Column<String> = varchar("color", 10)
    public val github: Column<String> = varchar("github", 1024)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public object DocVersions : IntIdTable("docs_version") {
    public val reference: Column<String> = varchar("version_reference", 255)
    public val project: Column<EntityID<String>> =
        reference("project_id", Projects, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    public val navigation: Column<Navigation> = serializable<Navigation>("navigation")
    public val stable: Column<Boolean> = bool("stable")
    public val recommended: Column<Boolean> = bool("recommended")

    init {
        uniqueIndex("ref_project_uq", reference, project)
    }
}

public object Pages : IntIdTable("pages") {
    public val pageId: Column<String> = varchar("page_id", 255)
    public val project: Column<EntityID<String>> =
        reference("project_id", Projects, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    public val version: Column<EntityID<Int>> =
        reference("version_id", DocVersions, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    public val content: Column<String> = text("content")
    public val title: Column<String> = text("title")
    public val subTitle: Column<String> = text("sub_title")
    public val summary: Column<PageSummary> = serializable<PageSummary>("summary")

    init {
        uniqueIndex("page_project_version_uq", pageId, project, version)
    }
}

public class ProjectEntity(id: EntityID<String>) : Entity<String>(id) {
    public companion object : EntityClass<String, ProjectEntity>(Projects)

    public var name: String by Projects.name
    public var color: String by Projects.color
    public var github: String by Projects.github
}

public class DocVersionEntity(id: EntityID<Int>) : IntEntity(id) {
    public companion object : IntEntityClass<DocVersionEntity>(DocVersions)

    public var reference: String by DocVersions.reference
    public var project: ProjectEntity by ProjectEntity referencedOn DocVersions.project
    public var navigation: Navigation by DocVersions.navigation
    public var stable: Boolean by DocVersions.stable
    public var recommended: Boolean by DocVersions.recommended
}

public class PageEntity(id: EntityID<Int>) : IntEntity(id) {
    public companion object : IntEntityClass<PageEntity>(Pages)

    public var pageId: String by Pages.pageId
    public var project: ProjectEntity by ProjectEntity referencedOn Pages.project
    public var version: DocVersionEntity by DocVersionEntity referencedOn Pages.version
    public var content: String by Pages.content
    public var title: String by Pages.title
    public var subTitle: String by Pages.subTitle
    public var summary: PageSummary by Pages.summary
}
