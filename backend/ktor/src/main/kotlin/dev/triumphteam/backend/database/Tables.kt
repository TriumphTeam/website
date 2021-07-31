@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.triumphteam.backend.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Projects : IntIdTable() {
    val name = varchar("name", 255)
}

object Entries : IntIdTable() {
    val project = reference("project", Projects, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val literal = varchar("literal", 255)
    val destination = varchar("destination", 255).nullable()
    val type = ubyte("type")
    val indent = uinteger("indent").default(0u)
    val position = uinteger("position")
}

object Pages : IntIdTable() {
    val project = reference("project", Projects, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val url = varchar("url", 255)
    val github = varchar("github", 2048)
    val content = text("content", "utf8_general_ci")
    val checksum = varchar("checksum", 64)
}

object Contents : IntIdTable() {
    val page = reference("page", Pages, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val literal = varchar("literal", 512)
    val indent = uinteger("indent").default(0u)
    val position = uinteger("position")
}
