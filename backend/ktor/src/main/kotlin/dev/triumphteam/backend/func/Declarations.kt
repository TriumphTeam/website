package dev.triumphteam.backend.func

import dev.triumphteam.backend.LOGGER
import dev.triumphteam.backend.config.BeanFactory
import dev.triumphteam.backend.database.Entries
import dev.triumphteam.markdown.summary.Entry
import dev.triumphteam.markdown.summary.Header
import dev.triumphteam.markdown.summary.Link
import dev.triumphteam.markdown.summary.Menu
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.request.receiveOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import me.mattstudios.config.properties.Property
import net.lingala.zip4j.core.ZipFile
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * Creates a CIO client
 */
fun makeClient() = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(kotlinx)
    }
}

/**
 * Sets up the serializer modules needed
 */
private val serializer = SerializersModule {
    polymorphic(Entry::class) {
        subclass(Header::class)
        subclass(Link::class)
        subclass(Menu::class)
    }
}

/**
 * Main Kotlinx json provider
 */
val kotlinx = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    serializersModule = serializer
}

/**
 * The original [receiveOrNull] still throws JsonException from Kotlinx.Serialization
 */
suspend inline fun <reified T : Any> ApplicationCall.receiveNullable(): T? {
    return try {
        receiveOrNull()
    } catch (e: Exception) {
        null
    }
}

private const val GITHUB_API = "https://api.github.com/"

/**
 * Gets the commits path from the api
 */
fun commits(repo: String) = "${GITHUB_API}repos/$repo/commits"

/**
 * Simple logging function
 */
fun log(message: () -> String) = LOGGER.info(message())

/**
 * Simple logging function for wanrings
 */
fun warn(message: () -> String) = LOGGER.warn(message())

/**
 * Unzips file into the output folder
 */
fun File.unzipTo(output: File) {
    val zipFile = ZipFile(this)
    zipFile.extractAll(parentFile.path)

    val mainFolder = parentFile.listFiles()?.firstOrNull { it.isDirectory } ?: return
    output.clear()
    Files.move(mainFolder.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING)
}

/**
 * Clears the content of a folder
 */
private fun File.clear() {
    deleteRecursively()
    mkdir()
}

/**
 * Bean factory function to create properties
 */
fun <B : Any> create(bean: BeanFactory<B>): Property<B> {
    return Property.create(bean.createDefault())
}

/**
 * Creates a folder if it doesn't exist
 */
fun folder(path: Path): File {
    val folder = path.toFile()
    if (!folder.exists()) folder.mkdirs()
    return folder
}

/**
 * Maps an entry from the result row from the database
 */
fun mapEntry(result: ResultRow): Entry? {
    return when (result[Entries.type]) {
        1.toUByte() -> result[Entries.destination]?.let {
            Link(result[Entries.literal], it)
        }
        2.toUByte() -> {

            val main = result[Entries.destination]?.let {
                Link(result[Entries.literal], it)
            } ?: return null

            val children = transaction {
                Entries.select { Entries.parent eq result[Entries.id] }
            }.mapNotNull { childResult ->
                childResult[Entries.destination]?.let {
                    Link(result[Entries.literal], it)
                }
            }

            Menu(main, children)
        }
        else -> Header(result[Entries.literal])
    }
}