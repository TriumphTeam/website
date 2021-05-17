@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.triumphteam.backend.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Projects : IntIdTable() {
    val name = varchar("name", 255)
}

object Indexes : IntIdTable() {
    val project = reference("project", Projects).uniqueIndex()
}

object Entries : IntIdTable() {
    val index = reference("index", Indexes)
    val text = varchar("text", 255)
    val type = ubyte("type")
    val parent = reference("parent", Entries).nullable()
    val position = uinteger("position")
}