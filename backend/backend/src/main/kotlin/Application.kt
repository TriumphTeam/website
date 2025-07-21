package dev.triumphteam.backend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.triumphteam.backend.database.Versions
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.database.Projects
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File
import java.util.Properties

public val DATA_FOLDER: File = File("data-ktor")

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
        SchemaUtils.createMissingTablesAndColumns(Projects, Versions, Pages)
        //MigrationUtils.statementsRequiredForDatabaseMigration(Projects, DocVersions, Pages)
    }

    embeddedServer(
        factory = CIO,
        module = Application::module,
        port = System.getenv("WEBSITE_PORT")?.toIntOrNull() ?: 8001,
        watchPaths = listOf("classes"),
    ).start(true)
}
