package dev.triumphteam.backend.feature

import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.func.commits
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.util.AttributeKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.mattstudios.config.SettingsManager

class Github(configuration: Configuration) {

    private val config = configuration.config
    private val client = configuration.client

    init {
        checkCommit()
    }

    private fun checkCommit() {
        GlobalScope.launch {
            val latestCommit = client.get<Array<Commit>>(commits(config[Settings.REPO])).firstOrNull() ?: return@launch

            if (config[Settings.LATEST_COMMIT] == latestCommit.sha) {
                println("Equals!")
                return@launch
            }

            config[Settings.LATEST_COMMIT] = latestCommit.sha
            config.save()
            println("Not equals!")
        }
    }

    @Serializable
    private data class Commit(
        val sha: String
    )

    class Configuration {
        lateinit var config: SettingsManager
            private set
        lateinit var client: HttpClient
            private set

        fun config(config: SettingsManager) {
            this.config = config
        }

        fun client(client: HttpClient) {
            this.client = client
        }
    }

    companion object Feature : ApplicationFeature<Application, Configuration, Github> {
        override val key: AttributeKey<Github> = AttributeKey("Github")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Github {
            return Github(Configuration().apply(configure))
        }
    }

}