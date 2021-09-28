package dev.triumphteam.backend.func

import dev.triumphteam.backend.LOGGER
import dev.triumphteam.backend.config.BeanFactory
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.project.projectType
import dev.triumphteam.backend.project.summary.Entry
import dev.triumphteam.backend.project.summary.Header
import dev.triumphteam.backend.project.summary.Item
import dev.triumphteam.backend.project.summary.UnorderedList
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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * Creates a CIO client
 */
fun makeClient() = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(JSON)
    }
}

fun String.titleCase() = replaceFirstChar { it.uppercase() }

/**
 * Sets up the serializer modules needed
 */
private val serializer = SerializersModule {
    polymorphic(Entry::class) {
        subclass(Header::class)
        subclass(Item::class)
        subclass(UnorderedList::class)
    }
}

/**
 * Main Kotlinx json provider
 */
val JSON = Json {
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

fun getProject(type: String, projectName: String): ResultRow? {
    return Projects.select {
        Projects.name eq projectName and (Projects.type eq type.projectType)
    }.firstOrNull()
}

fun getPage(type: String, projectName: String, page: String): ResultRow? {
    val project = getProject(type, projectName) ?: return null

    return Pages.select {
        Pages.project eq project[Projects.id]
    }.andWhere {
        Pages.url eq page
    }.firstOrNull()
}
