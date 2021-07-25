@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.triumphteam.backend.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Projects : IntIdTable() {
    val name = varchar("name", 255)
}

object Entries : IntIdTable() {
    val project = reference("project", Projects)
    val literal = varchar("literal", 255)
    val destination = varchar("destination", 255).nullable()
    val type = ubyte("type")
    val indent = integer("indent").default(0)
    val parent = reference("parent", Entries, ReferenceOption.CASCADE).nullable()
    val position = uinteger("position")
}