package dev.triumphteam.backend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.triumphteam.backend.api.database.DocVersions
import dev.triumphteam.backend.api.database.Pages
import dev.triumphteam.backend.api.database.Projects
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.Properties

public val DATA_FOLDER: File = File("data")

public fun main() {
    // Database connection
    Database.connect(
        HikariDataSource(
            HikariConfig(
                Properties().apply {
                    setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
                    setProperty("dataSource.user", System.getenv("DB_USER") ?: "matt")
                    setProperty("dataSource.password", System.getenv("DB_PASS") ?: "test")
                    setProperty("dataSource.databaseName", System.getenv("DB_NAME") ?: "website")
                    setProperty("dataSource.portNumber", System.getenv("DB_PORT") ?: "5432")
                    setProperty("dataSource.serverName", System.getenv("DB_SERVER") ?: "localhost")
                }
            )
        )
    )

    // Creates all the tables
    transaction {
        SchemaUtils.create(
            Projects,
            DocVersions,
            Pages,
        )
    }

    embeddedServer(Netty, module = Application::module, port = 8001, watchPaths = listOf("classes")).start(true)
}
