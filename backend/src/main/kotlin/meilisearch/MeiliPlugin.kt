package dev.triumphteam.backend.meilisearch

import io.ktor.http.URLProtocol
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.application
import io.ktor.server.application.plugin
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

public class Meili(config: Configuration) {

    public val client: MeiliClient = config.createClient()

    public class Configuration {
        private var host: String = "0.0.0.0"
        private var port: Int = 7700
        private var apiKey: String = "masterKey"
        private var protocol: URLProtocol = URLProtocol.HTTP

        internal fun createClient() = MeiliClient(host, port, apiKey, protocol)
    }

    public companion object Plugin : BaseApplicationPlugin<Application, Configuration, Meili> {

        override val key: AttributeKey<Meili> = AttributeKey("Meili")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Meili {
            return Meili(Configuration().apply(configure))
        }
    }
}

public suspend inline fun <reified T> PipelineContext<*, ApplicationCall>.search(
    index: String,
    query: String,
    filter: String? = null,
): List<T> = with(this.application.plugin(Meili).client) {
    return index(index).search(query, filter)
}

public suspend inline fun PipelineContext<*, ApplicationCall>.index(index: String): MeiliClient.Index =
    application.plugin(Meili).client.index(index)
