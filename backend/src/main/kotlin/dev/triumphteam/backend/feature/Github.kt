package dev.triumphteam.backend.feature

import dev.triumphteam.backend.CONFIG
import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.func.commits
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.unzipTo
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.util.AttributeKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path

@ExperimentalPathApi
class Github(private val client: HttpClient) {

    private val downloadFolder = createOrGet(Path("data", "download"))
    private val repoFolder = createOrGet(Path("data", "repo"))

    init {
        checkCommit()
    }

    private fun checkCommit() {
        GlobalScope.launch {
            val latestCommit =
                client.get<Array<Commit>>(commits(CONFIG[Settings.REPO].name)).firstOrNull() ?: return@launch

            if (CONFIG[Settings.REPO].latestCommit == latestCommit.sha && !repoFolder.listFiles().isNullOrEmpty()) {
                return@launch
            }

            log { "New commit found." }
            cloneRepository(downloadFolder)

            CONFIG[Settings.REPO].latestCommit = latestCommit.sha
            CONFIG.save()
        }
    }

    private fun cloneRepository(folder: File) {
        URL(CONFIG[Settings.REPO].downloadLink).openStream().use { input ->
            val zip = Path(folder.path, "repo.zip").toFile()
            FileOutputStream(zip).use { output ->
                log { "Downloading repository." }
                input.copyTo(output)
                log { "Downloading done, saved to `${zip.path}`." }
            }
            log { "Unzipping repository." }
            zip.unzipTo(repoFolder)
            zip.delete()
            log { "Unzipped and deleted zip file." }
        }
    }

    private fun createOrGet(path: Path): File {
        val folder = path.toFile()
        if (!folder.exists()) folder.mkdirs()
        return folder
    }

    /**
     * Commit data class to simplify getting the commit hash
     */
    @Serializable
    private data class Commit(
        val sha: String
    )

    /**
     * Simple configuration for the feature builder
     */
    class Configuration {
        lateinit var client: HttpClient
    }

    /**
     * Feature companion
     */
    companion object Feature : ApplicationFeature<Application, Configuration, Github> {
        override val key = AttributeKey<Github>("Github")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Github {
            return Github(Configuration().apply(configure).client)
        }
    }

}