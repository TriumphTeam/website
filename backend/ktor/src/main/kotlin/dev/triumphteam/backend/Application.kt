package dev.triumphteam.backend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.database.Entries
import dev.triumphteam.backend.database.Projects
import io.ktor.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import me.mattstudios.config.SettingsManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths

// Application's logger
val LOGGER: Logger = LoggerFactory.getLogger("backend")

fun main() {
    val dataSource = HikariDataSource(hikariConfig)
    Database.connect(dataSource)

    // Creates all the tables
    transaction {
        SchemaUtils.create(
            Projects,
            Entries,
        )
    }

    embeddedServer(CIO, module = Application::module, port = 8000).start(true)
}

// Applications config
val CONFIG = SettingsManager
    .from(Paths.get("data", "config.yml"))
    .configurationData(Settings::class.java)
    .create()

// Hikari configuration from the config file
private val hikariConfig = HikariConfig().apply {
    val databaseConfig = CONFIG[Settings.DATABASE]
    dataSourceClassName = "org.mariadb.jdbc.MariaDbDataSource"
    jdbcUrl = "jdbc:mariadb://${databaseConfig.host}:${databaseConfig.port}/"
    username = databaseConfig.username
    password = databaseConfig.password
    isAutoCommit = false
    addDataSourceProperty("databaseName", databaseConfig.database)
}
