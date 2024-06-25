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
import java.util.Properties

public fun main() {
    // Database connection
    Database.connect(
        HikariDataSource(
            HikariConfig(
                Properties().apply {
                    setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
                    setProperty("dataSource.user", System.getProperty("DB_USER") ?: "matt")
                    setProperty("dataSource.password", System.getProperty("DB_PASS") ?: "test")
                    setProperty("dataSource.databaseName", System.getProperty("DB_NAME") ?: "website")
                    setProperty("dataSource.portNumber", System.getProperty("DB_PORT") ?: "5432")
                    setProperty("dataSource.serverName", System.getProperty("DB_SERVER") ?: "localhost")
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
