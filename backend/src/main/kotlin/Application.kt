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
                    setProperty("dataSource.user", "matt")
                    setProperty("dataSource.password", "test")
                    setProperty("dataSource.databaseName", "website")
                    setProperty("dataSource.portNumber", "5432")
                    setProperty("dataSource.serverName", "localhost")
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

    embeddedServer(Netty, module = Application::module, port = 8001, watchPaths = listOf("classes"))
        .start(true)
}
