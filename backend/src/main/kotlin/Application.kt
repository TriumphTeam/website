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

    val user = System.getenv("DB_USER")
    val pass = System.getenv("DB_PASS")
    val name = System.getenv("DB_NAME")
    val port = System.getenv("DB_PORT")
    val server = System.getenv("DB_SERVER")

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
