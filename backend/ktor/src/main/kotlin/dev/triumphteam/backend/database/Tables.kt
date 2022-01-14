@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.triumphteam.backend.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Projects : Table() {
    val id = varchar("project_id", 255)
    val name = varchar("name", 255)
    val icon = varchar("icon", 1024)
    val version = varchar("version", 10)
    val type = uinteger("type").default(0u)
    val color = varchar("color", 255)
    val github = varchar("github", 1024)
    val summary = text("summary")

    override val primaryKey = PrimaryKey(id)
}

object Pages : IntIdTable() {
    val project = reference("project", Projects.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val url = varchar("url", 255)
    val github = varchar("github", 2048)
    val content = text("content")
    val checksum = varchar("checksum", 64)
}

object Contents : IntIdTable() {
    val page = reference("page", Pages, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val literal = varchar("literal", 512)
    val href = varchar("href", 1024)
    val indent = uinteger("indent").default(0u)
    val position = uinteger("position")
}
