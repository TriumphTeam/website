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

    val user = System.getProperty("DB_USER")
    val pass = System.getProperty("DB_PASS")
    val name = System.getProperty("DB_NAME")
    val port = System.getProperty("DB_PORT")
    val server = System.getProperty("DB_SERVER")

    println(user)
    println(pass)
    println(name)
    println(port)
    println(server)

    // Database connection
    Database.connect(
        HikariDataSource(
            HikariConfig(
                Properties().apply {
                    setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
                    setProperty("dataSource.user", user ?: "matt")
                    setProperty("dataSource.password", pass ?: "test")
                    setProperty("dataSource.databaseName", name ?: "website")
                    setProperty("dataSource.portNumber", port ?: "5432")
                    setProperty("dataSource.serverName", server ?: "localhost")
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
