package dev.triumphteam.backend.func

import dev.triumphteam.backend.LOGGER
import dev.triumphteam.backend.config.BeanFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import me.mattstudios.config.properties.Property
import net.lingala.zip4j.core.ZipFile
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
 * Main Kotlinx json provider
 */
val kotlinx = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private const val GITHUB_API = "https://api.github.com/"


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

fun folder(path: Path): File {
    val folder = path.toFile()
    if (!folder.exists()) folder.mkdirs()
    return folder
}