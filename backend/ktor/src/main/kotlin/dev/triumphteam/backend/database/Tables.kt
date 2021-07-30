@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.triumphteam.backend.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Projects : IntIdTable() {
    val name = varchar("name", 255)
}

object Entries : IntIdTable() {
    val project = reference("project", Projects)
    val literal = varchar("literal", 255)
    val destination = varchar("destination", 255).nullable()
    val type = ubyte("type")
    val indent = uinteger("indent").default(0u)
    val position = uinteger("position")
}

object Pages : IntIdTable() {
    val project = reference("project", Projects)
    val url = varchar("url", 255)
    val content = text("content", "utf8_general_ci")
    val checksum = varchar("checksum", 64)
}

object Contents : IntIdTable() {
    val page = reference("page", Pages)
    val literal = varchar("literal", 512)
    val indent = uinteger("indent").default(0u)
    val position = uinteger("position")
}
