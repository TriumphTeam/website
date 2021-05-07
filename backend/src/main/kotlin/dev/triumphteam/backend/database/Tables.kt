package dev.triumphteam.backend.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Projects : IntIdTable() {
    val name = varchar("name", 255)
}